package es.keensoft.alfresco.behaviour;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.repo.transaction.AlfrescoTransactionSupport;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.repo.transaction.TransactionListener;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.transaction.TransactionService;
import org.alfresco.util.transaction.TransactionListenerAdapter;

import es.keensoft.alfresco.model.SignModel;

public class TransactionalDeleteBehaviour implements NodeServicePolicies.BeforeDeleteNodePolicy {
 
	// Key to identify resources associated to transaction
	private static final String KEY_RELATED_NODES = TransactionalDeleteBehaviour.class.getName() + ".relatedNodes";
	 
	private PolicyComponent policyComponent;
	private NodeService nodeService;
	private TransactionService transactionService;
	private TransactionListener transactionListener;
	private ThreadPoolExecutor threadPoolExecutor;
	 
	// Bind behaviour and initialize transaction listener
	public void init() {
	     
	    policyComponent.bindClassBehaviour(
	            NodeServicePolicies.BeforeDeleteNodePolicy.QNAME, 
	            SignModel.ASPECT_SIGNED, 
	            new JavaBehaviour(this, "beforeDeleteNode", Behaviour.NotificationFrequency.FIRST_EVENT));
	     
	    this.transactionListener = new RelatedNodesTransactionListener();
	     
	}
	 
	
	@Override
	public void beforeDeleteNode(NodeRef nodeRef) {
	     
	    // Bind listener to current transaction
	    AlfrescoTransactionSupport.bindListener(transactionListener);
	
	    // Get some related nodes to work with
	    List<NodeRef> relatedNodes = new ArrayList<NodeRef>();
	    for (AssociationRef signatureAssoc : nodeService.getTargetAssocs(nodeRef, SignModel.ASSOC_SIGNATURE)) {
	        relatedNodes.add(signatureAssoc.getTargetRef());
	    }
	
	    // Transactions involving several nodes need resource updating
	    List<NodeRef> currentRelatedNodes = AlfrescoTransactionSupport.getResource(KEY_RELATED_NODES);
	    if (currentRelatedNodes == null) {
	        currentRelatedNodes = relatedNodes;
	    } else {
	        currentRelatedNodes.addAll(relatedNodes);
	    }
	     
	    // Put resources to be used in transaction listener
	    AlfrescoTransactionSupport.bindResource(KEY_RELATED_NODES, currentRelatedNodes);
	
	}
	 
	// Listening "afterCommit" transaction event
	private class RelatedNodesTransactionListener 
	    extends TransactionListenerAdapter implements TransactionListener {
	
	    @Override
	    public void afterCommit() {
	        @SuppressWarnings("unchecked")
	        List<NodeRef> nodesToBeReviewed = 
	            (List<NodeRef>) AlfrescoTransactionSupport.getResource(KEY_RELATED_NODES);
	        if (nodesToBeReviewed != null) {
	            for (NodeRef nodeToBeReviewed : nodesToBeReviewed) {
	                // Launch every node work in a different thread
	                Runnable runnable = new RelatedNodeDeletion(nodeToBeReviewed);
	                threadPoolExecutor.execute(runnable);
	            }
	        }
	    }
	     
	    @Override
	    public void flush() {
	    }
	     
	}
	 
	// Thread to work with an individual node
	private class RelatedNodeDeletion implements Runnable {
	     
	    private NodeRef nodeToBeReviewed;
	     
	    private RelatedNodeDeletion(NodeRef nodeToBeReviewed) {
	        this.nodeToBeReviewed = nodeToBeReviewed;
	    }
	
	    @Override
	    public void run() {
	        AuthenticationUtil.runAsSystem(new RunAsWork<Void>() {
	             
	            public Void doWork() throws Exception {
	                 
	                RetryingTransactionCallback<Void> callback = new RetryingTransactionCallback<Void>() {
	                     
	                    @Override
	                    public Void execute() throws Throwable {
	                        if(nodeService.exists(nodeToBeReviewed)) {
	                    		nodeService.deleteNode(nodeToBeReviewed);
	                    	}
	                        return null;
	                    }
	                };
	                 
	                try {
	                    RetryingTransactionHelper txnHelper = 
	                        transactionService.getRetryingTransactionHelper();
	                    txnHelper.doInTransaction(callback, false, true);
	                } catch (Throwable e) {
	                    e.printStackTrace();
	                }
	                 
	                return null;
	                 
	            }
	        });
	    }
	     
	}

	public void setPolicyComponent(PolicyComponent policyComponent) {
		this.policyComponent = policyComponent;
	}


	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}


	public void setTransactionService(TransactionService transactionService) {
		this.transactionService = transactionService;
	}


	public void setTransactionListener(TransactionListener transactionListener) {
		this.transactionListener = transactionListener;
	}


	public void setThreadPoolExecutor(ThreadPoolExecutor threadPoolExecutor) {
		this.threadPoolExecutor = threadPoolExecutor;
	}
}
import { NodesApiService } from '@alfresco/adf-core';
import { Component, Input, OnInit, ViewEncapsulation } from '@angular/core';
import { MinimalNodeEntryEntity } from 'alfresco-js-api';

import { SignatureOptions } from './signature.model';
import { SignatureService } from './signature.service';

// miniapplet.js loaded from index.html
declare var MiniApplet: any;

@Component({
    selector: 'app-signature',
    templateUrl: './signature.component.html',
    encapsulation: ViewEncapsulation.None
})

// Launch client signature program for an Alfresco node
export class SignatureComponent implements OnInit {

    @Input() node: MinimalNodeEntryEntity;

    // Internal data
    dataToSign: any;
    nodeInfo: Node;
    options: SignatureOptions;
    mimeType: string;
    fileName: string;

    // Message management
    messageVisible: boolean;
    message: string;

    constructor(
        private nodesApi: NodesApiService,
        private signatureService: SignatureService) {

      this.messageVisible = false;
      MiniApplet.cargarMiniApplet();

    }

    ngOnInit() {
      this.initializeNode(this.node.name, this.node.id);
      this.initializeSignatureOptions();
    }

    // Get Base64 content, mime type and file name from Alfresco Repository
    initializeNode(name: string, id: string) {

        this.signatureService.getFileContent(id).subscribe (
            data => {
                const reader = new FileReader();
                reader.onloadend = (e) => {
                    this.dataToSign = btoa(reader.result);
                };
                reader.readAsBinaryString(data);
            }
        );

        this.nodesApi.getNode(id, null).subscribe(
            data => {
                this.mimeType = data.content.mimeType;
                if (this.mimeType === 'application/pdf') {
                    this.mimeType = 'pdf';
                }
                this.fileName = data.name;
            }
        );
    }

    // Get signature options (CAdES / PAdES) from Alfresco Repository
    initializeSignatureOptions() {
        this.signatureService.getSignatureOptions().subscribe(
            data => {
                this.options = new SignatureOptions().toSignatureOptions(data);
            }
        );
    }

    // Miniapplet success callback: save signed document and signer certificate data in Alfresco Repository
    saveSigned(signatureBase64, certificateBase64) {

        this.signatureService.saveSignature(
            this.node.id,
            certificateBase64,
            this.mimeType,
            '1',
            signatureBase64
        ).subscribe(
            res => {
              this.message = 'Success: File signed!';
              this.messageVisible = true;
            },
            err => {
                this.message = 'Error: ' + err;
                this.messageVisible = true;
            }
        );

    }

    // Miniapplet error callback: save signed document and signer certificate data in Alfresco Repository
    showError(errorType, errorMessage) {
        this.message = 'Error: ' + errorType + ' (' + errorMessage + ')';
        this.messageVisible = true;
    }

    // Sign button: launch local AutoFirma program to perform and attached (PAdES for PDF) or detached (CAdES for other mimetypes) signature
    sign() {
        const signedCallback = this.saveSigned.bind(this);
        const signedError = this.showError.bind(this);
        if (this.mimeType === 'pdf') {
            const paramsPades = this.options.paramsPades.split('\t').join('\n');
            MiniApplet.sign(this.dataToSign, this.options.signatureAlg, 'PAdES', paramsPades, signedCallback, signedError);
        } else {
            const paramsCades = this.options.paramsCades.split('\t').join('\n');
            MiniApplet.sign(this.dataToSign, this.options.signatureAlg, 'CAdES', paramsCades, signedCallback, signedError);
        }
    }

}

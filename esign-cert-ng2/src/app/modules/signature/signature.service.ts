import { AlfrescoApiService, AppConfigService } from '@alfresco/adf-core';
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/Observable';

@Injectable()
export class SignatureService {

    // Alfresco host from 'ecmHost' configuration entry, like http://localhost:8080
    ecmHost: string;

    constructor(
        private http: HttpClient,
        private alfrescoApi: AlfrescoApiService,
        private appConfig: AppConfigService) {
            this.ecmHost = this.appConfig.get<string>('ecmHost');
        }

    // Get raw file content for an Alfresco Node Id (Standar REST API)
    public getFileContent(id: string): Observable<Blob> {

        const ticket = this.alfrescoApi.getInstance().getTicketEcm();

        return this.http.get(
            this.ecmHost +
            '/alfresco/api/-default-/public/alfresco/versions/1/nodes/' + id +
            '/content?attachment=true&alf_ticket=' + ticket, {
                responseType: 'blob'
            }
        );

    }

    // Get signature options from alfresco-global.properties (Custom REST API)
    public getSignatureOptions(): Observable<any> {

        const ticket = this.alfrescoApi.getInstance().getTicketEcm();

        return this.http.get(
            this.ecmHost + '/alfresco/s/keensoft/sign/signature-params?alf_ticket=' + ticket
        );

    }

    // Save signature and signer data in Alfresco Repository (Custom REST API)
    public saveSignature(id: string,
        certificateBase64: string,
        mimeType: string,
        signerPosition: string,
        signatureBase64: string): Observable<any> {

        const ticket = this.alfrescoApi.getInstance().getTicketEcm();

        return this.http.post(
            this.ecmHost + '/alfresco/s/keensoft/sign/save-sign?alf_ticket=' + ticket, {
                nodeRef: 'workspace://SpacesStore/' + id,
                signedData: signatureBase64,
                signerData: certificateBase64,
                mimeType: mimeType,
                signerPostition: signerPosition
            }
        );
    }

}

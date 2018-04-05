export class SignatureOptions {

    paramsCades: string;
    paramsPades: string;
    signatureAlg: string;
    firstSignaturePosition: string;
    secondSignaturePosition: string;
    thirdSignaturePosition: string;
    fourthSignaturePosition: string;
    fifthSignaturePosition: string;
    sixthSignaturePosition: string;
    signaturePurposeEnabled: boolean;

    toSignatureOptions(r: any): SignatureOptions {
        const options = <SignatureOptions>({
          paramsCades: r.paramsCades,
          paramsPades: r.paramsPades,
          signatureAlg: r.signatureAlg,
          firstSignaturePosition: r.firstSignaturePosition,
          secondSignaturePosition: r.secondSignaturePosition,
          thirdSignaturePosition: r.thirdSignaturePosition,
          fourthSignaturePosition: r.fourthSignaturePosition,
          fifthSignaturePosition: r.fifthSignaturePosition,
          sixthSignaturePosition: r.sixthSignaturePosition,
          signaturePurposeEnabled: r.signaturePurposeEnabled
        });
        return options;
      }

}


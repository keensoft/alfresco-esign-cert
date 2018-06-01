# EsignCertNg2: ADF Component for Alfresco ESign Cert addon

This is an Angular 5 Component to be used within an Alfresco ADF application.

## Basic Usage

** Dependency **

Install the module in your ADF application by including dependency in `package.json` file...

```
"dependencies": {
  "alfresco-esign-cert-ng2": "1.0.1"
}
```

... or by using command line in your app root folder

```
$ npm install alfresco-esign-cert-ng2 --save
```

** TAG Selector **

```html
<app-signature
    [node]="aMinimalNodeEntryEntity"
</app-signature>
```

### Properties

| Name | Type | Description |
| ---- | ---- | ----------- |
| node | [MinimalNodeEntryEntity](https://github.com/Alfresco/alfresco-js-api/blob/master/src/alfresco-core-rest-api/docs/NodeMinimalEntry.md) | The node to perform a signature. |

## Compatibility Matrix

| esign-cert-repo | component | ADF |
| --- | --- | --- |
| 1.6.2 | 1.0.0 | 2.2.0 |


### Static resources

As this component relies on [Client @firma](https://github.com/ctt-gob-es/clienteafirma/), external resources have to be added to `assets/` folder.

Create `assets/js` folder in your Angular App and copy both `miniapplet.js` and `miniapplet-full_X_X_X.jar` files inside.

Include also the depencency manually in your `index.html`.

```html
<head>
  <script type="text/javascript" src="assets/js/miniapplet.js"></script>
</head>
```

## Development notes

This project was generated with [Angular CLI](https://github.com/angular/angular-cli) version 1.6.6.

### Development server

Run `ng serve` for a dev server. Navigate to `http://localhost:4200/`. The app will automatically reload if you change any of the source files.

### Packaging

Run `npm run packagr` to build artifacts that will be stored in the `dist/` directory. Use the `-prod` flag for a production build.

You can use `npm pack` from inside `dist/` folder to produce a compressed file ready to be installed for local development.

```
$ npm install /github/keensoft/alfresco-esign-cert/esign-cert-ng2/dist/keensoft-esign-cert-ng2-1.0.0.tgz
```

### Further help

To get more help on the Angular CLI use `ng help` or go check out the [Angular CLI README](https://github.com/angular/angular-cli/blob/master/README.md).

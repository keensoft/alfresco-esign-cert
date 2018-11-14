# Alfresco eSign Cert

<table width="100%">
    <tr>
        <td width="120">Developer:</td>
        <td>Angel Borroy, Daniel Fernández</td>
        <td colspan="2"></td>
    </tr>
    <tr>
        <td width="120">Release:</td>
        <td><a href="https://github.com/keensoft/alfresco-esign-cert/tree/1.1.1">1.1.1</a></td>
        <td width="120">Release date:</td>
        <td>2016-09-23</td>
    </tr>
    <tr>
        <td width="120">Type:</td>
        <td>Extension for Repository</td>
        <td width="120">Packaging:</td>
        <td>AMP</td>
    </tr>
    <tr>
        <td width="120">Public listing:</td>
        <td colspan="3"><a href="https://addons.alfresco.com/addons/alfresco-esign-cert">addons.alfresco.com/addons/alfresco-esign-cert</a></td>
    </tr>
    <tr>
        <td width="120">Source hosting:</td>
        <td><a href="https://github.com/keensoft/alfresco-esign-cert">GitHub keensoft/alfresco-esign-cert</a></td>
        <td width="120">Source license:</td>
        <td><a href="http://www.gnu.org/licenses/lgpl-3.0.html">LGPL v3.0</a></td>
    </tr>
    <tr>
        <td width="120">Reviewer:</td>
        <td>Angel Borroy</td>
        <td width="120">Review date:</td>
        <td>2016-09-23</td>
    </tr>
    <tr>
        <td>Criteria catalog:</td>
        <td colspan="3"><a href="https://github.com/OrderOfTheBee/addons/wiki/Inclusion-criteria-overview">Criteria overview</a> (state of 2015-01-06)</td>
    </tr>
</table>

<p><span style="font-size:150%;">Review result: </span><span class="label labelstyle-159818 linked-labelstyle-159818 lightertooltipped" style="background-color: #159818; color: #fff;">PASS</span></p>

## General notes

No | Note | Reference
--: | ---- | ---------
 1 | GNU License is not applied correctly | [LGPL v3.0](http://www.gnu.org/licenses/lgpl-3.0.html)

## Criteria notes

This segment lists all notes / issues / violations of inclusion criteria found during the review. Any item of the [criteria catalog](https://github.com/OrderOfTheBee/addons/wiki/Inclusion-criteria-overview) not mentioned in this section is presumed to be matched sufficiently for inclusion in the OOTB addon listing.

Class | No | Category | [Type](https://github.com/OrderOfTheBee/addons/wiki/General-guidelines#requirement-relevance-types) | Note | 
----- | --: | -------- | :----- | ----
Non-technical | 10 | [Documentation](https://github.com/OrderOfTheBee/addons/wiki/Non-technical-inclusion-criteria#documentation) | must | Many users having problems on installation, specially on signing the applet component
Non-technical | 11 | [Documentation](https://github.com/OrderOfTheBee/addons/wiki/Non-technical-inclusion-criteria#documentation) | must | Required AutoFirma tool and Cliente movil @firma are described but no tested versions or integration methods are detailed

## Compatibility

This segments lists all notes regarding compatibility of the addon with Alfresco, 3rd-party addons or client environments.

Compatible with | Version | Notes
--- | --- | ---
Alfresco | Community 5.1.x | tested by reviewer
Alfresco | Community 5.0.x | tested by reviewer
Chrome (only Windows) | 53 | tested by reviewer
IE (only Windows) | 11 | tested by reviewer
Firefox | 48 | tested by reviewer
Safari | 10 | tested by reviewer
AutoFirma (Windows) | 1.4.2 | tested by reviewer
AutoFirma (Mac) | 1.4.2 | FAIL

## Suggested tasks

1. Correct license application within project
2. Althought JSP pages are supported for Share extension, it should be nice to traduce this part to Aikau pages to provide a better technology stack
3. Provide a detailed (and tested) guide for installation by including Applet signature
4. Include tested versions for 3rd party software (AutoFirma & Cliente movil @firma)
### Description ###

**JRightLog** utility parse ANY [Oracle Hyperion Essbase](http://www.oracle.com/us/solutions/ent-performance-bi/business-intelligence/essbase/index.html)® v.5 - **v.11.1.1** server or application logs and generates a full, custom delimited, output for enhanced analysis (database, spreadsheet).

**JRightLog** is intended to help Essbase® and Planning® database administrators and developers.
Any Essbase® generated log may be parsed whatever the delimiter defined in Essbase.cfg config file (please refer to `DELIMITEDMSG TRUE`, `DELIMITER` in Oracle’s Essbase® _Technical Reference_).
The idea was to make the logs really readable (one liner) by facilitating their integration and filtering in a spreadsheet (common logical delimiter).
Reconciled logs may be inserted as well in a relational database for querying or statistics production.

**JRightLog** jar utility is inspired from the PERL version of [essbaserightlog](http://code.google.com/p/essbaserightlog/).

### Options available ###

  * Advanced date formatting (US, European or standard ISO),
  * Headers insertion,
  * Detailed message categories (please refer to _Essbase Server and Application Log Message Categories_ in DBAG),
  * String filtering,
  * Custom separator,

### Availability ###

**JRightLog** is available in the downloads section as a Java JAR application. It requires JRE 1.6 or upper to run. **JRightLog** provide a command line access and a GUI interface as well [(print screen)](http://code.google.com/p/jrightlog/wiki/PrintScreen). At the moment **JRightLog** has been successfully tested on Windows environments only (XP, Vista).

### From the same author ###

[jssauditmerger](http://code.google.com/p/jssauditmerger/) - Merge your spreadsheet audit logs for better analysis (Java version)

[ssauditmerger](http://code.google.com/p/ssauditmerger/) - Merge your spreadsheet audit logs for better analysis (PERL version)

[essbaserightlog](http://code.google.com/p/essbaserightlog/) - Parse ANY Oracle Hyperion Essbase® server or application logs and generates a full, custom delimited, output for enhanced analysis (database, spreadsheet) (PERL version)
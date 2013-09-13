Docsetter
=========
A [Dash](http://kapeli.com/dash) [docset](http://kapeli.com/docsets) generator.

Currently only used for indexing Salesforce developer documentation, but can be used for any type of documentation.

Usage
-----
Generate a docset:

    $ sbt "run -n <NAME>"

Docsets
--------
These are docsets generated with Docsetter. Make sure [Dash](http://kapeli.com/dash) is installed and then run the `open` command below:

 - Apex: `$ open dash-feed://https%3A%2F%2Fs3.amazonaws.com%2Fsfdc-docsets%2FApex.xml`
 - Visualforce: `$ open dash-feed://https%3A%2F%2Fs3.amazonaws.com%2Fsfdc-docsets%2FVisualforce.xml`
 
All copyrights and trademarks are reserved by their respective owners.

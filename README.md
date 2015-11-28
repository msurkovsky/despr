# despr
Visual programming tool based on stream processing. Easily extendable and useful for mass data processing.

## The idea
A program created in **despr** is composed from blocks -- operations -- interconnected in a directed acyclic graph. This graph describe a complex algorighm consisting of ''elementary'' blocks. Every operation can have input and onput parameters. Operations without input parameters are meant as generators or loaders. On the other hand, operations without output parametrs can store the resulting data into filesystem or send them to a database.

Originally the tool were used for image processig, thus it provides a collecition of operations dealing with images. However, the usage is not limited only to this purpose. It depends on a user which problem they solve and which type of operations they use.

## Tool structure
Despr is consited from several modules. The basic modul is an **editor**. By default there is no operation. It is let up to a user which operartion they will use.

![alt tag](https://raw.github.com/msurkovsky/despr/master/web-doc/img/despr_overview.png)

### Editor structure
* manifest.mf
* build-despr.xml -- and script for building the editor.
* despr-app.properties
* despr-build.properties
* src/ -- source files.
* lib/ -- libraries which are neded when the editor starts.
* extensions/ -- plugins which can be (un)mount during runtime.
* resources/ -- settings, icons, etc.

### Build editor
`ant -f build-despr.xml`

### TODO: 
Write documentaion for:
* build extensions
* install extensions
* create own operation
* add new type
* ...

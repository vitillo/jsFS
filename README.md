jsFS
====

The Javascript FileSystem Library provides access to the host filesystem through a java applet. 

Even though HTML5 provides the File API, it isn't possible for a javascript application to manipulate the filesystem directly and indipendently of the user. Furthermore, uploading directories through an input element works only under Chrome and Firefox (webkitdirectory and mozdirectory) so that's not a viable solution and in any case it requires user interaction.

Inspired by filer.js, the applet's API uses the familiar UNIX command names (cwd, cd, ls, cp, rm, mkdir) as its interface. Reading and writing of files can be accomplished with the read and write methods. 
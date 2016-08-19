# EASE Jupyter Kernels Developer Documentation

## Publishing data to Jupyter

### Jupyter principles

The result of a command executed via Jupyter will be returned from the EASE kernel.
In case of a multipart command only the result of the last statement is returned.

These Jupyter results are distributed as dictionaries mapping from [MIME types](https://en.wikipedia.org/wiki/Media_type) to the actual data.
Clients can then decide what representation suits their needs best. 
For example a simple Jupyter text console will not have need of a "image/png" object and will rather choose the "text/plain" representation.
The Jupyter notebook on the other hand is very interactive and will choose the image over the text.

EASE module developers that are interested in Jupyter might want their results to be published in the nicest possible way.

The following sections will give an overview on how to publish data from your code in a simple way.

### IJupyterPublishable

To simplify these different representations an interface [IJupyterPublishable](org.eclipse.ease.jupyter.kernel/src/org/eclipse/ease/jupyter/kernel/handlers/IJupyterPublishable.java) was introduced.
This interface only contains a single method _**Map<String, Object>**_ _toMimeTypeDict()_.
As the name suggest this method returns the different MIME type representation of an Object.

EASE module developers can have their module methods return _IJupyterPublishable_ objects and they will be automatically distributed to the connected Jupyter clients.
This approach is especially useful for newly developed modules because the interface can be added during the initial implementation phase.

### Adapters

For existing modules or any type of Java class another approach is to implement an _Adapter_.
Adapters are used in Eclipse to cast objects of one type to another (for Jupyter this type is of course _IJupyterPublishable_).

If a result object does not implement the _IJupyterPublishable_ interface, the kernel will try to find a matching adapter.

To make such an adapter available you must implement the intterface _org.eclipse.core.runtime.IAdapterFactory_. 
The interface contains two methods:

+ **<T> T** _getAdapter(**Object** adaptableObject, **Class<T>** adapterType)_
+ **Class<?>[]** _getAdapterList()_

_getAdapaterList_ must return an array of all classes this adapter can create casts from.

_getAdapter_ handles the actual casting of the given object.
The first parameter _adaptableObject_ is the object that shall be casted.
The second parameter _adapterType_ is the desired class to cast to.

Have a look at the implementation of [JupyterAdapterFactory](org.eclipse.ease.jupyter.kernel/src/org/eclipse/ease/jupyter/kernel/handlers/JupyterAdapterFactory.java) to see a simple example.

Once the _IAdapterFactory_ is implemented, it further needs to be registered in Eclipse.

To do this, open the **MANIFEST.MF** and a new extension _org.eclipse.core.runtime.adapters\*_.

For the _factory_ you need to set the _adaptableType_ to the java class you want to cast from and the  _class\*_ to your _IAdapterFactory_ class.

You then need to add an _adapter_ to your _factory_. The value for its _type\*_ field must be _org.eclipse.ease.jupyter.kernel.handlers.IJupyterPublishable_.

In some cases the adapter will not work yet. In this case make sure you checked _Activate this plug-in when one of its classes is loaded_ in the _Overview_ tab of your **MANIFEST.MF**.


### Fallback

If the result does not implement the _IJupyterPublishable_ interface and no adapter was found, the kernel will fall back to sending the string representation of an object.

The result will then be a dictionary with only one key "text/plain" and a value of whatever _result.toString()_ returns.



## EASE Jupyter Flow

This section will give a quick overview on the execution flow when starting Jupyter from eclipse.

It shall further give information about the reasons we implemented the framework the way we did.

### Jupyter <-> EASE (basics)

Jupyter clients communicate with kernels over [ZMQ sockets](http://zeromq.org/).

When a client needs a kernel, Jupyter will start an executable and pass along the information on which sockets the kernel should listen on.

This is a first problematic point for the Eclipse integration, as we want to start up Eclipse before we start Jupyter.
As Jupyter can only start kernels using executables we came up with the following workaround.

Eclipse will start up a [Dispatcher](org.eclipse.ease.jupyter.kernel/src/org/eclipse/ease/jupyter/kernel/Dispatcher.java) that listens on a randomly chosen port.

When Jupyter needs a client, it will trigger a launcher that connects to this port and passes the Jupyter information along to the _Dispatcher_.
This launcher executable can be found in the [org.eclipse.ease.jupyter.kernel.launcher](org.eclipse.ease.jupyter.kernel.launcher) project.

The launcher itself is fairly straight forward, it will be called with a list of command line parameters among which are the host and port for the _Dispatcher_. It then connects to this socket and sends all necessary information. It then waits until the _Dispatcher_ is shut down. This is necessary because otherwise Jupyter will think that the kernel died when the executable stops.

Once the _Dispatcher_ has all necessary information it will create the actual [Kernel](org.eclipse.ease.jupyter.kernel/src/org/eclipse/ease/jupyter/kernel/Kernel.java) with an _IScriptEngine_.
The _Kernel_ is doing all the actual work a Jupyter kernel is suppossed to do. For further information on this either check the implementation here or read the [Jupyter Documentation](https://ipython.org/ipython-doc/3/development/kernels.html).


### Jupyter <-> EASE (advanced)
The previous section described the basic steps necessary to establish a connection between Jupyter and EASE. 

As we want to have kernels for different script engines and Eclipse instances we needed to extend this tough.

The solution we came up with only works from the Eclipse UI but as this is necessary for the script engines anyways this is not an issue.

We use the existing Jupyter notebook file format (*.ipynb) and extend it with additional information about the script engine to be launched.

Notebook files are basically JSON files with a defined set of keys. One of these keys is called **metadata** and can store additonal information. We introduced a new sub-dictionary *ease_info* there where we add the IScriptEngine ID we want to launch. This information will be set during the creation of the *.ipynb file so this only works from the _New File Wizard_ (without manually manipulating the file).

Once the _*.ipynb_ file is started from Eclipse, its contents will be parsed and if the *meta_data/ease_info* dictionary is present the following procedure is initiated.

First an _IScriptEngine_ is created based on the value from the *.ipynb file.

Then a template Jupyter kernel directory is copied to a new temporary folder. This directory contains all files necessary for registering as a Jupyter kernel. The main file for this is _kernel.json_ that contains information about how to launch the kernel (see the [Jupyter Documentation](https://ipython.org/ipython-doc/3/development/kernels.html#kernel-specs)). The file contains a template with all necessary information but still needs to be populated to suite the new kernel.

The information that is being populated is:

+ The absolute path to the _org.eclipse.ease.jupyter.kernel.launcher.jar_ file to be used. The absolute path is necessary because Jupyter would otherwise not be able to find it.
+ The port the _Dispatcher_ is listening on. This port is chosen at random.
+ The name of the kernel. This will be set to the IScriptEngine name for users to be able to check that they selected the right kernel.

Next a new **jupyter** process will be created with its working directory being the current workspace's root directory. The port for the Jupyter server is once again chosen at random.

For the created process, the environment variable *JUPYTER_PATH* is set to the previously created temporary directory. This is necessary for Jupyter to find the correct EASE kernel (and only the kernel for this instance). The *JUPYTER_PATH* is extended so all other previously set kernel locations will still work.

Once the Jupyter server is running a matching _Dispatcher_ is created using the port previously chosen with the _IScriptEngine_ from the notebook file.

Next an URL mapping to the notebook file is created. This URL has the format: 
**http://localhost:_ChosenJupyterPort_/notebooks/_PathToNotebookFile_**
where _ChosenJupyterPort_ is the generated port of the Jupyter notebook server and _PathToNotebookFile_ is a relative path form the workspace root location to the notebook file.

Jupyter only has access to files via relative paths based on its working directory. This means that any file in the workspace will work fine but linked files might give problems.

Finally a Eclipse browser widget is opened with the URL to the notebook file.

The whole process is rather cumbersome but ensures that multiple Jupyter and Eclipse instances can be used at the same time because each notebook file has its own Jupyter server with specialized information.

Once the browser closes all steps are reversed:

+ The **jupyter** process is stopped.
+ The _Dispatcher_ is shutdown.
+ The temporary kernel directory is deleted.


## (Potential) Troubleshooting

In most cases the behaviour described in **Jupyter <-> EASE (advanced)** is perfectly fine, but if Eclipse shuts down abruptly and forcefully it might be that either the **jupyter** process is still running or the temporary kernel directory is not deleted.

Neither of these things should be an issue but for users that want to clean up their system the following tricks can help:

### Kill Jupyter Notebooks:

**jupyter-notebook _list_** will output a list of currently running Jupyter servers.
If you find any servers that should not be there try to close the corresponding processes.

Under _Linux_ (and most likely _Mac_) you can use: fuser -k _PORT_/tcp

Under _Windows_ you will probably use **netstat** to find the _PID_ for the process holding the Jupyter port and then kill said process using e.g. _taskmanager_.

### Clear Temporary Kernel Directories:

To remove any old temporary kernel directories you need to look for directories named **ease\_jupyter\_kernel\__RANDOMNUMBER_** in your system's temporary directory.

Under _Linux_ (and most likely _Mac_) this will most likely be: _/tmp_ or _/var/tmp_

Under _Windows_ this typically is _C:\Users\**USERNAME**\AppData\Local\Temp_. It is probably easier to type _%TEMP%_ into the address bar of an explorer window.

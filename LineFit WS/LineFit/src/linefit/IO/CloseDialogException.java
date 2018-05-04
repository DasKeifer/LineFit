package linefit.IO;

/** An Exception so we can deal better with closing dialogs and not exploding.
* We don't want to do anything when we close the dialog that is why it is an empty class
* but we want to be able to catch it so we know when it was closed so we can act accordingly
* @author	Keith Rice
* @version	1.0
* @since 	&lt;0.98.0
*/
@SuppressWarnings("serial")
class CloseDialogException extends Exception 
{
	
}
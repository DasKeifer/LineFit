package linefit.IO;

/** An Exception so we can deal better with closing dialogs and not exploding. We don't want to do anything when we
 * close the dialog that is why it is an empty class but we want to be able to catch it so we know when it was closed so
 * we can act accordingly
 * 
 * @author Das Keifer
 * @version 1.0
 * @since 0.99.0 */
@SuppressWarnings("serial")
public class CloseDialogException extends Exception
{
}
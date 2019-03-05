/* Copyright (C) 2013 Covenant College Physics Department
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General License for more details.
 * 
 * You should have received a copy of the GNU Affero General License along with this program. If not, see
 * http://www.gnu.org/licenses/. */

package linefit.IO;


/** An Exception so we can deal better with closing dialogs and not exploding. We don't want to do anything when we
 * close the dialog that is why it is an empty class but we want to be able to catch it so we know when it was closed so
 * we can act accordingly
 * 
 * @author Keith Rice
 * @version 1.0
 * @since 0.99.0 */
@SuppressWarnings("serial")
public class CloseDialogException extends Exception
{
}
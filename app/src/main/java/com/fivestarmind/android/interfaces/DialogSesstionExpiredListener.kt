package com.fivestarmind.android.interfaces

import com.fivestarmind.android.enum.DialogEventType

/**
 * Interface definition for a callback to be invoked when a user performs action in Dialog.
 */
interface DialogSesstionExpiredListener {
    /**
     * Called when an dialog's event view type has been clicked
     *
     * @param dialogEventType event type of the dialog which has been clicked
     * @param requestCode
     * @param model
     */
    fun onDialogSesstionExpiredListener(dialogEventType: DialogEventType)

}
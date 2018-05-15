@file:JvmName("ArchExt")

package es.iessaladillo.pedrojoya.pr057.extensions

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProviders
import android.support.v4.app.FragmentActivity

inline fun <reified T : ViewModel>FragmentActivity.getViewModel(): T {
    return ViewModelProviders.of(this).get(T::class.java)
}

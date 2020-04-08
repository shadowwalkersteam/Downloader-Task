package com.rapidzz.kidcap.Utils.Application


import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.net.ConnectivityManager
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel

import androidx.lifecycle.ViewModelProviders
import com.rapidzz.kidcap.Utils.factory.ViewModelFactory
import android.widget.*
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.rapidzz.kidcap.Models.DataModels.GeneralModels.DDItem
import com.rapidzz.kidcap.Models.DataModels.UtilityModels.ApiErrorResponse
import com.rapidzz.kidcap.Models.DataModels.UtilityModels.ErrorResponse
import com.rapidzz.kidcap.R
import com.rapidzz.kidcap.Utils.GeneralUtils.ErrorUtils
import com.rapidzz.kidcap.Utils.NetworkUtils.ResultWrapper
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException


fun <T : ViewModel> AppCompatActivity.obtainViewModel(viewModelClass: Class<T>) =
    ViewModelProviders.of(this, ViewModelFactory.getInstance(application)).get(viewModelClass)

fun <T : ViewModel> FragmentActivity.obtainViewModel(viewModelClass: Class<T>) =
    ViewModelProviders.of(this, ViewModelFactory.getInstance(application)).get(viewModelClass)

fun <T : ViewModel> Fragment.obtainViewModel(viewModelClass: Class<T>) =
    ViewModelProviders.of(this, ViewModelFactory.getInstance(this.activity?.application!!)).get(
        viewModelClass
    )



fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}


fun EditText.isValidPhone(): Boolean {
    return (!TextUtils.isEmpty(text.toString().trim { it <= ' ' })
            && Patterns.PHONE.matcher(text.toString().trim { it <= ' ' }).matches()
            && text.toString().length >= 13)
}

fun EditText.isEmailValid(): Boolean {
    return !TextUtils.isEmpty(text) && Patterns.EMAIL_ADDRESS.matcher(text).matches()
}



fun ImageView.loadImage(url:String) {
    val circularProgressDrawable = context?.let { CircularProgressDrawable(it) }
    circularProgressDrawable?.strokeWidth = 5f
    circularProgressDrawable?.centerRadius = 30f
    circularProgressDrawable?.start()
    context?.let {
            Glide.with(it)
                .load(url)
                .placeholder(circularProgressDrawable)
                .into(this)
        }

}

fun ImageView.loadImage(resourceID: Int) {
    Glide.with(context)
        .load(resourceID)
        .into(this)
}

fun TextView.showStrikeThrough(show: Boolean) {
    paintFlags =
        if (show) paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        else paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
}




fun<T> Call<T>.enqueue(callback: CallBackKt<T>.() -> Unit) {
    val callBackKt = CallBackKt<T>()
    callback.invoke(callBackKt)
    this.enqueue(callBackKt)
}

class CallBackKt<T>: Callback<T> {

    var onSucessResponse: ((Response<T>) -> Unit)? = null
    var onErrorResponse: ((String?) -> Unit)? = null
    var onFailure: ((t: Throwable?) -> Unit)? = null

    override fun onFailure(call: Call<T>, t: Throwable) {
        onFailure?.invoke(t)
    }

    override fun onResponse(call: Call<T>, response: Response<T>) {
        if(response.isSuccessful)
        {
            if(response.body()!=null)
            {
                onSucessResponse?.invoke(response)
            }
            else if(response.errorBody()!=null)
            {
                onErrorResponse?.invoke(response.errorBody()?.string())
            }
            else
            {
                onFailure?.invoke(Throwable("No error mentioned"))
            }

        }
        else
        {
            onErrorResponse?.invoke(response.errorBody()?.string())
        }
    }

}





fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}

fun View.gone() {
    this.visibility = View.GONE
}

fun View.isVisibleToUser():Boolean
{
    return visibility==View.VISIBLE
}

fun EditText.string():String
{
    return this.text.toString()
}

fun EditText.Error(errorMessage:String?)
{
    error=context.getString(R.string.field_req)
    errorMessage?.let {
        error=errorMessage
    }
    requestFocus()
}


fun AutoCompleteTextView.findId(allValues:ArrayList<DDItem>):Int
{
    var id=1
   allValues.find { it.name.equals(text.toString(),true) }?.let {
       id=it.id
   }

    return id

}






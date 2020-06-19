package com.rapidzz.garageapp.Views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import com.afollestad.vvalidator.form
import com.google.android.material.snackbar.Snackbar
import com.rapidzz.garageapp.Models.Source.Repository.UserDataSource
import com.rapidzz.garageapp.R
import com.rapidzz.garageapp.Utils.Application.*
import com.rapidzz.garageapp.Utils.GeneralUtils.DialogUtils
import com.rapidzz.garageapp.Utils.GeneralUtils.SessionManager
import com.rapidzz.garageapp.ViewModels.BaseAndroidViewModel
import com.rapidzz.garageapp.Views.activities.MainActivity
import com.rapidzz.garageapp.Views.activities.RegistrationActivity
import com.rapidzz.garageapp.Views.dialog.AlertMessageDialog

import kotlin.collections.ArrayList


abstract class BaseFragment : Fragment() {

    lateinit var dialog: AlertDialog
    lateinit var sessionManager: SessionManager














    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dialog = DialogUtils.getProgressDialog(requireContext())
        sessionManager = SessionManager(requireContext())

    }


    abstract fun initViews()

    abstract fun attachViewModel()





    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(getLayoutId(), container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleToolBar()
        initViews()
        attachViewModel()


    }

    abstract fun getLayoutId():Int


    fun showProgressDialog(show: Boolean) {

        if (dialog != null && show) {
            if (!dialog.isShowing)
                dialog.apply {
                    show()
                }
        } else if (dialog != null && !show) {
            if (dialog.isShowing)
                dialog.dismiss()
        }
    }












    fun applyValidations(
        viewList: ArrayList<View>,
        buttonView: Button,
        callback: UserDataSource.OnValidationCallback
    ) {
        form {
            for (view in viewList) {
                if (view is EditText) {
                    input(view) {
                        isNotEmpty().description(getString(R.string.field_req))
                    }
                } else if (view is Spinner) {
                    spinner(view) {
                        selection().greaterThan(0)
                        onErrors { view, errors ->
                            if (errors.isNotEmpty()) {
                                showToast("Some selections are missing")
                            }
                        }
                    }
                }
            }

            submitWith(buttonView) { result ->
                var results: ArrayList<String> = ArrayList()
                for (value in result.values()) {
                    results.add(value.asString())
                }
                callback.onApplied(true, results)
            }
        }
    }





    fun navigateToFragment(action: Int, bundle: Bundle?) {
        if(activity is RegistrationActivity) {
            val navController = Navigation.findNavController(
                activity as RegistrationActivity,
                R.id.nav_host_fragment
            )
            navController.navigate(action, bundle)
        }
        else {
            val navController = Navigation.findNavController(activity as MainActivity, R.id.nav_host_fragment)
            navController.navigate(action, bundle)
        }
    }







    fun handleToolBar()
    {
        if(requireActivity() is RegistrationActivity)
        {
           if(this is StartedFragment)
           {
               (requireActivity()as RegistrationActivity).hideToolbar(true)
           }
            else
           {
               (requireActivity()as RegistrationActivity).hideToolbar(false)
           }
        }
        else
        {

            if(this is NewOrderFragment)
            {
                (requireActivity()as MainActivity).needBackMove=false
                (requireActivity()as MainActivity).hideToolbar(false)
            }
            else
            {
                (requireActivity()as MainActivity).needBackMove=true
                (requireActivity()as MainActivity).hideToolbar(true)
            }

        }
    }




    protected fun setupGeneralViewModel(generalViewModel:BaseAndroidViewModel)
    {

        with(generalViewModel)
        {
            snackbarMessage.observe(viewLifecycleOwner, Observer {
                it.getContentIfNotHandled()?.let {
                    showAlertDialog(it)
                }
            })

            progressBar.observe(viewLifecycleOwner, Observer {
                it.getContentIfNotHandled()?.let {
                    showProgressDialog(it)
                }
            })
        }
    }



}

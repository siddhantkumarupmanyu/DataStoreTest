package com.example.datastore

import android.graphics.drawable.Animatable2
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import com.example.datastore.databinding.FragmentFirstBinding

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpPassword()

        binding.imageView.setOnClickListener(object : View.OnClickListener {
            var passwordHidden = true

            @RequiresApi(Build.VERSION_CODES.M)
            override fun onClick(v: View?) {

                if (passwordHidden) {
                    binding.imageView.setImageResource(
                        R.drawable.avd_anim_show
                    )
                } else {
                    binding.imageView.setImageResource(
                        R.drawable.avd_anim_hide
                    )
                }

                val drawable = binding.imageView.drawable as Animatable2
                drawable.start()

                drawable.registerAnimationCallback(object : Animatable2.AnimationCallback() {
                    override fun onAnimationEnd(drawable: Drawable?) {
                        super.onAnimationEnd(drawable)

                        if (passwordHidden) {
                            binding.imageView.setImageResource(R.drawable.visibility_off_temp)
                        } else {
                            binding.imageView.setImageResource(R.drawable.visibility_on)
                        }
                        passwordHidden = !passwordHidden
                    }
                })
            }
        })
    }

    private fun setUpPassword() {
        val on =
            AppCompatResources.getDrawable(requireContext(), R.drawable.visibility_on)!!
        // // val visibilityOffDrawable =
        // //     AppCompatResources.getDrawable(requireContext(), R.drawable.visiblity_off)
        //
        //
        // on.callback
        //
        binding.password.setCompoundDrawablesWithIntrinsicBounds(null, null, on, null)

        // binding.password.setOnTouchListener(
        //
        // )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}

// https://medium.com/@ali.muzaffar/understanding-vectordrawable-pathdata-commands-in-android-d56a6054610e

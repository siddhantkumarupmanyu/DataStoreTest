package com.example.datastore

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.example.datastore.databinding.FragmentFirstBinding

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

        setUpImageView()
    }

    private fun setUpImageView() {
        binding.imageView.setImageDrawable(
            AnimatedVectorDrawableCompat.create(
                requireContext(),
                R.drawable.anim_cut_the_eye
            )!!
        )

        binding.imageView.setOnClickListener(object : View.OnClickListener {
            var isCut = false

            override fun onClick(v: View?) {
                val drawable = binding.imageView.drawable as AnimatedVectorDrawableCompat

                drawable.registerAnimationCallback(object : Animatable2Compat.AnimationCallback() {
                    override fun onAnimationEnd(drawable: Drawable?) {
                        super.onAnimationEnd(drawable)

                        isCut = !isCut

                        val avd: AnimatedVectorDrawableCompat = if (isCut) {
                            AnimatedVectorDrawableCompat.create(
                                requireContext(),
                                R.drawable.anim_remove_the_cut
                            )!!
                        } else {
                            AnimatedVectorDrawableCompat.create(
                                requireContext(),
                                R.drawable.anim_cut_the_eye
                            )!!
                        }
                        binding.imageView.setImageDrawable(avd)
                    }
                })

                drawable.start()
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

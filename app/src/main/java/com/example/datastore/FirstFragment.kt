package com.example.datastore

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
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

        setUpPasswordView(binding.passwordImageView, binding.password)
    }

    private fun setUpPasswordView(imageView: ImageView, editText: EditText) {
        imageView.setImageDrawable(
            AnimatedVectorDrawableCompat.create(
                requireContext(),
                R.drawable.anim_cut_the_eye
            )!!
        )

        imageView.setOnClickListener(object : View.OnClickListener {
            var isCut = false

            override fun onClick(v: View?) {
                val drawable = imageView.drawable as AnimatedVectorDrawableCompat

                drawable.registerAnimationCallback(object : Animatable2Compat.AnimationCallback() {
                    override fun onAnimationEnd(d: Drawable?) {
                        super.onAnimationEnd(d)

                        drawable.unregisterAnimationCallback(this)

                        isCut = !isCut

                        val avd: AnimatedVectorDrawableCompat

                        if (isCut) {
                            avd = AnimatedVectorDrawableCompat.create(
                                requireContext(),
                                R.drawable.anim_remove_the_cut
                            )!!

                            editText.performAndRestoreCursorPosition {
                                editText.inputType =
                                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                            }

                        } else {
                            avd = AnimatedVectorDrawableCompat.create(
                                requireContext(),
                                R.drawable.anim_cut_the_eye
                            )!!

                            editText.performAndRestoreCursorPosition {
                                editText.inputType =
                                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                            }

                        }
                        imageView.setImageDrawable(avd)
                    }
                })

                drawable.start()
            }
        })
    }

    private fun EditText.performAndRestoreCursorPosition(operation: () -> Unit) {
        val selectionStart = this.selectionStart
        val selectionEnd = this.selectionEnd
        operation.invoke()
        this.setSelection(selectionStart, selectionEnd)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}

// for reference

// https://medium.com/@ali.muzaffar/understanding-vectordrawable-pathdata-commands-in-android-d56a6054610e
// https://gist.github.com/nickbutcher/b1806905c6bc0ef29f545fd580935bd3
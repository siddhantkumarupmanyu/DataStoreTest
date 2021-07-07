package com.example.datastore

import android.graphics.drawable.Animatable
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
import com.example.datastore.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpPasswordView(binding.passwordImageView, binding.password)
    }

    private fun setUpPasswordView(imageView: ImageView, editText: EditText) {
        val startingDrawable = AnimatedVectorDrawableCompat.create(
            requireContext(),
            R.drawable.anim_cut_the_eye
        )!!

        val callback = object : Animatable2Compat.AnimationCallback() {

            var isCut = false

            override fun onAnimationEnd(drawable: Drawable?) {
                super.onAnimationEnd(drawable)

                // cast with as? as drawable is nullable
                val t = drawable as? AnimatedVectorDrawableCompat
                t?.unregisterAnimationCallback(this)

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

                avd.registerAnimationCallback(this)
                imageView.setImageDrawable(avd)
            }
        }

        imageView.setImageDrawable(startingDrawable)

        startingDrawable.registerAnimationCallback(callback)

        imageView.setOnClickListener {
            val drawable = imageView.drawable as Animatable
            drawable.start()
        }
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
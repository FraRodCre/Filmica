package com.example.fcojavierrodriguez.filmica.view.util

import android.animation.Animator

class AnimatorEndListener(
    val callback: (Animator) -> Unit
) : Animator.AnimatorListener {
    override fun onAnimationRepeat(animation: Animator?) {
    }

    override fun onAnimationEnd(animator: Animator) {
        callback.invoke(animator)
    }

    override fun onAnimationCancel(animation: Animator?) {
    }

    override fun onAnimationStart(animation: Animator?) {
    }

}

package read.xiaotu.com.bookshelf.animation;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;

import read.xiaotu.com.bookshelf.R;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 15/7/12.
 */
public class AnimationTestActivity extends Activity {

    Button mBtnStartAnim;
    View mViewAnim;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.anim_test);

        mBtnStartAnim = (Button) findViewById(R.id.anim_test_btn);
        mViewAnim = findViewById(R.id.anim_test_view);

        mBtnStartAnim.setOnClickListener(mListener);
    }

    float mStartX = 0;
    float mStartY = 0;

    View.OnClickListener mListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ObjectAnimator animX = ObjectAnimator.ofFloat(mViewAnim, "translationX", mStartX, mStartX + 100);
            ObjectAnimator animY = ObjectAnimator.ofFloat(mViewAnim, "translationY", mStartY, mStartY + 50);
            mStartX += 100;
            mStartY += 50;
            AnimatorSet set = new AnimatorSet();
            set.playTogether(animX, animY);
            set.setDuration(300);
            set.setInterpolator(new AccelerateDecelerateInterpolator());
            set.start();
        }
    };
}

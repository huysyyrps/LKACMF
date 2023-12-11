package com.example.lkacmf.activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.lkacmf.R
import com.example.lkacmf.fragment.AnalystsFragment
import com.example.lkacmf.fragment.HomeFragment
import com.example.lkacmf.fragment.UserInfoFragment
import com.example.lkacmf.util.*
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity(), View.OnClickListener {
    private lateinit var mFm: FragmentManager
    private val mFragmentList = ArrayList<Fragment>()
    private val mFragmentTagList = arrayOf("OneFragment", "TwoFragment", "ThreeFragment")
    private lateinit var mCurrentFragmen: Fragment  // 记录当前显示的Fragment

    companion object {
        fun actionStart(context: Context) {
            val intent = Intent(context, MainActivity::class.java)
            context.startActivity(intent)
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mFragmentList.add(0, HomeFragment())
        mFragmentList.add(1, AnalystsFragment())
        mFragmentList.add(2, UserInfoFragment())
        mCurrentFragmen = mFragmentList[0]
        // 初始化首次进入时的Fragment
        mFm = supportFragmentManager;
        val transaction: FragmentTransaction = mFm.beginTransaction()
        transaction.add(R.id.frameLayout, mCurrentFragmen, mFragmentTagList[0])
        transaction.commitAllowingStateLoss()
        //tabLayout选择监听
        tabLayoutSelect()

        imageView.setOnClickListener(this)
    }

    private fun tabLayoutSelect() {
        tbLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.position) {
                    0->{
                        switchFragment(mFragmentList[0], mFragmentTagList[0])
                        return
                    }
                    1->{
                        switchFragment(mFragmentList[1], mFragmentTagList[1])
                        return
                    }
                    2->{
                        switchFragment(mFragmentList[2], mFragmentTagList[2])
                        return
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.imageView -> {
                drawer_layout.openDrawer(GravityCompat.START)
            }
        }
    }

    // 转换Fragment
    fun switchFragment(to: Fragment, tag: String?) {
        if (mCurrentFragmen !== to) {
            val transaction = mFm.beginTransaction()
            if (!to.isAdded) {
                // 没有添加过:
                // 隐藏当前的，添加新的，显示新的
                transaction.hide(mCurrentFragmen).add(R.id.frameLayout, to, tag).show(to)
            } else {
                // 隐藏当前的，显示新的
                transaction.hide(mCurrentFragmen).show(to)
            }
            mCurrentFragmen = to
            transaction.commitAllowingStateLoss()
        }
    }
}
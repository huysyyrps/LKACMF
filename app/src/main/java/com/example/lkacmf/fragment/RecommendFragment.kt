package com.example.lkacmf.fragment

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.lkacmf.R
import com.example.lkacmf.activity.ConfigurationActivity
import com.example.lkacmf.databinding.FragmentRecommendBinding
import com.example.lkacmf.util.file.BaseFileUtil
import com.example.lkacmf.util.sp.BaseSharedPreferences
import java.io.File

/**
 * 推荐配置
 */
@RequiresApi(Build.VERSION_CODES.O)
class RecommendFragment : Fragment(), View.OnClickListener {
    private var _binding: FragmentRecommendBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentRecommendBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStart() {
        super.onStart()
        binding.btnRecommend.setOnClickListener(this)
        var workpieceFrom = BaseSharedPreferences.get("nsWorkpieceFrom", "")
        var workpieceQuality = BaseSharedPreferences.get("nsWorkpieceQuality", "")
        var thickness = BaseSharedPreferences.get("ncThickness", "")
        var heat = BaseSharedPreferences.get("ncHeat", "")

        var probeHeat = ""
        var probeFrom = ""
        probeHeat = if (heat.isNotEmpty()&&heat.toFloat() < 100) {
            "常温"
        } else {
            "高温"
        }
        if (workpieceFrom == "管本体") {
            probeFrom = "硬质多阵列探头"
        } else if (workpieceFrom == "板本体") {
            probeFrom = "牙齿多阵列探头"
        } else if (workpieceFrom == "管对接" || workpieceFrom == "板对接") {
            probeFrom = "标准探头"
        } else if (workpieceFrom == "角焊缝" || workpieceFrom == "管座焊缝") {
            probeFrom = "笔式探头"
        }
        binding.tvProbeSelect.text = "$probeHeat$probeFrom"
        if (workpieceQuality=="碳钢"||workpieceQuality=="不锈钢"){
            binding.tvProbeRate.text = "2KHz"
        }else if (workpieceQuality=="铝"){
            binding.tvProbeRate.text = "1KHz"
        }else if (workpieceQuality=="钛"){
            binding.tvProbeRate.text = "5KHz"
        }


        if (thickness.isNotEmpty()){
            val thicknessNum = thickness.toFloat()
            if (thicknessNum>0&&thicknessNum<2){
                binding.tvProbeNumb.text = "12V"
            }else if (thicknessNum>2&&thicknessNum<4){
                binding.tvProbeNumb.text = "14V"
            }else if (thicknessNum>4&&thicknessNum<6){
                binding.tvProbeNumb.text = "16V"
            }else if (thicknessNum>6&&thicknessNum<8){
                binding.tvProbeNumb.text = "18V"
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnRecommend->{
                (activity as ConfigurationActivity).setViewPageItem(2)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
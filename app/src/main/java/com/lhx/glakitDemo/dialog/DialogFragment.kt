package com.lhx.glakitDemo.dialog

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import com.alibaba.fastjson.JSONObject
import com.lhx.glakit.api.HttpJSONTask
import com.lhx.glakit.base.fragment.BaseFragment
import com.lhx.glakit.base.widget.BaseContainer
import com.lhx.glakit.utils.AlertUtils
import com.lhx.glakit.utils.ToastUtils
import com.lhx.glakitDemo.R
import com.lhx.glakitDemo.databinding.DialogFragmentBinding

class DialogFragment: BaseFragment() {

    private val viewBinding by lazy { DialogFragmentBinding.bind(getContainerContentView()!!) }

    override fun initialize(
        inflater: LayoutInflater,
        container: BaseContainer,
        saveInstanceState: Bundle?
    ) {
        setBarTitle("Dialog")
        setContainerContentView(R.layout.dialog_fragment)
        viewBinding.apply {
            alert.setOnClickListener{
                AlertUtils.alert(
                    "标题",
                    "副标题",
                    getDrawableCompat(R.mipmap.ic_launcher_round),
                    arrayOf("取消", "确定"),
                    0
                ) { position ->
                    ToastUtils.showToast(it, "点击第 $position 个")
                }.show(childFragmentManager)
            }

            actionSheet.setOnClickListener{
                AlertUtils.actionSheet(
                    "标题",
                    "副标题",
                    getDrawableCompat(R.mipmap.ic_launcher_round),
                    arrayOf("删除"),
                    0
                ){position ->
                    ToastUtils.showToast(it, "点击第 $position 个")
                }.show(childFragmentManager)
            }

            popover.setOnClickListener{
                PopoverMenu(requireContext()).show(it, true)
            }

            popup.setOnClickListener{
                val task = HomeTask()
                task.onSuccess = {
                    val task = it as HomeTask
                    
                }
//                ListPopupWindow(requireContext()).showAsDropDown(it)
            }
        }
    }

    private class HomeTask: HttpJSONTask() {

        override fun processJSON(json: JSONObject): Boolean {
            Log.d("json", json.toString())
            return true
        }

        override val currentURL: String
            get() = "https://devtest.zegobird.com:11111/api/index"

        override fun prepare() {

        }
    }
}
package com.zhuwenhao.flipped.widget

import android.app.Activity
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.util.TypedValue
import android.view.MenuItem
import android.widget.RemoteViews
import android.widget.Toast
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.afollestad.materialdialogs.input.input
import com.afollestad.materialdialogs.list.listItems
import com.zhuwenhao.flipped.R
import com.zhuwenhao.flipped.db.ObjectBox
import com.zhuwenhao.flipped.ext.toColorHex
import com.zhuwenhao.flipped.view.ColorChooserView
import io.objectbox.Box

class TextWidgetConfigureFragment : PreferenceFragmentCompat() {

    private lateinit var prefTitle: Preference
    private lateinit var prefTitleSize: Preference
    private lateinit var prefTitleColor: Preference
    private lateinit var prefAlignment: Preference

    private lateinit var textSizeList: Array<CharSequence>
    private lateinit var textAlignmentList: Array<CharSequence>
    private var textAlignment = 4

    private lateinit var twBox: Box<TextWidget>

    private var widgetId = 0
    private var textWidget: TextWidget? = null

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.pref_text_widget)
        initPref()
        initData()
    }

    private fun initPref() {
        prefTitle = findPreference("prefTitle")!!
        prefTitle.setOnPreferenceClickListener {
            MaterialDialog(requireContext()).show {
                title(text = it.title.toString())
                input(hintRes = R.string.widget_title_check_hint, prefill = it.summary, inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE) { _, text ->
                    it.summary = text
                }
                positiveButton(android.R.string.ok)
                negativeButton(android.R.string.cancel)
            }

            true
        }

        textSizeList = requireContext().resources.getTextArray(R.array.pref_list_text_size)

        prefTitleSize = findPreference("prefTitleSize")!!
        prefTitleSize.summary = textSizeList[0]
        prefTitleSize.setOnPreferenceClickListener {
            showTextSizeSingleChoiceDialog(it)

            true
        }

        prefTitleColor = findPreference("prefTitleColor")!!
        prefTitleColor.summary = Color.WHITE.toColorHex()
        prefTitleColor.setOnPreferenceClickListener {
            showColorChooserDialog(it)

            true
        }

        textAlignmentList = requireContext().resources.getTextArray(R.array.pref_list_text_alignment)

        prefAlignment = findPreference("prefAlignment")!!
        prefAlignment.summary = textAlignmentList[textAlignment]
        prefAlignment.setOnPreferenceClickListener {
            MaterialDialog(requireContext()).show {
                title(text = it.title.toString())
                listItems(R.array.pref_list_text_alignment) { _, index, text ->
                    textAlignment = index
                    it.summary = text
                }
            }

            true
        }
    }

    private fun initData() {
        widgetId = requireActivity().intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)

        twBox = ObjectBox.boxStore.boxFor(TextWidget::class.java)
        textWidget = twBox.query().equal(TextWidget_.widgetId, widgetId.toLong()).build().findFirst()

        if (textWidget != null) {
            prefTitle.summary = textWidget!!.title
            prefTitleSize.summary = textWidget!!.titleSize
            prefTitleColor.summary = textWidget!!.titleColor
            prefAlignment.summary = textAlignmentList[textWidget!!.textAlignment]
            textAlignment = textWidget!!.textAlignment
        }
    }

    private fun showTextSizeSingleChoiceDialog(preference: Preference) {
        MaterialDialog(requireContext()).show {
            title(text = preference.title.toString())
            listItems(R.array.pref_list_text_size) { _, _, text ->
                preference.summary = text
            }
        }
    }

    private fun showColorChooserDialog(preference: Preference) {
        val dialog = MaterialDialog(requireContext())
                .title(text = preference.title.toString())
                .customView(R.layout.dialog_color_chooser)
                .positiveButton(android.R.string.ok) { dialog ->
                    preference.summary = (dialog.getCustomView() as ColorChooserView).color.toColorHex()
                }
                .negativeButton(android.R.string.cancel)
        (dialog.getCustomView() as ColorChooserView).setColorARGB(Color.parseColor(preference.summary.toString()))
        dialog.show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> activity?.onBackPressed()
            R.id.menu_apply -> {
                if (prefTitle.summary == null) {
                    Toast.makeText(context, R.string.widget_title_check_hint, Toast.LENGTH_SHORT).show()
                } else {
                    if (textWidget == null) {
                        twBox.put(TextWidget(widgetId = widgetId,
                                title = prefTitle.summary.toString(),
                                titleSize = prefTitleSize.summary.toString(),
                                titleColor = prefTitleColor.summary.toString(),
                                textAlignment = textAlignment))
                    } else {
                        textWidget?.title = prefTitle.summary.toString()
                        textWidget?.titleSize = prefTitleSize.summary.toString()
                        textWidget?.titleColor = prefTitleColor.summary.toString()
                        textWidget?.textAlignment = textAlignment
                        twBox.put(textWidget!!)
                    }
                    updateWidgetUI()
                }
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun updateWidgetUI() {
        val intent = Intent(context, TextWidgetConfigureActivity::class.java)
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
        val pendingIntent = PendingIntent.getActivity(context, widgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val views = RemoteViews(requireContext().packageName, when (textAlignment) {
            0 -> R.layout.widget_text_top_left
            1 -> R.layout.widget_text_top_right
            2 -> R.layout.widget_text_bottom_left
            3 -> R.layout.widget_text_bottom_right
            4 -> R.layout.widget_text_center
            5 -> R.layout.widget_text_center_top
            6 -> R.layout.widget_text_center_bottom
            7 -> R.layout.widget_text_center_left
            8 -> R.layout.widget_text_center_right
            else -> R.layout.widget_text_center
        })
        views.setOnClickPendingIntent(R.id.textTitle, pendingIntent)
        views.setTextViewText(R.id.textTitle, prefTitle.summary.toString())
        views.setTextViewTextSize(R.id.textTitle, TypedValue.COMPLEX_UNIT_SP, prefTitleSize.summary.toString().toFloat())
        views.setTextColor(R.id.textTitle, Color.parseColor(prefTitleColor.summary.toString()))

        AppWidgetManager.getInstance(context).updateAppWidget(widgetId, views)

        val resultIntent = Intent()
        resultIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
        requireActivity().setResult(Activity.RESULT_OK, intent)
        requireActivity().finish()
    }
}
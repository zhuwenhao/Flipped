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
import com.zhuwenhao.flipped.R
import com.zhuwenhao.flipped.db.ObjectBox
import com.zhuwenhao.flipped.view.ColorChooserView
import io.objectbox.Box

class TextWidgetConfigureFragment : PreferenceFragmentCompat() {

    private lateinit var prefTitle: Preference
    private lateinit var prefTitleSize: Preference
    private lateinit var prefTitleColor: Preference

    private lateinit var textSizeList: Array<CharSequence>

    private lateinit var twBox: Box<TextWidget>

    private var widgetId = 0
    private var textWidget: TextWidget? = null

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.pref_text_widget)
        initPref()
        initData()
    }

    private fun initPref() {
        prefTitle = findPreference("prefTitle")
        prefTitle.setOnPreferenceClickListener {
            MaterialDialog.Builder(context!!)
                    .title(it.title)
                    .input(getString(R.string.widget_title_check_hint), it.summary, false) { _, input ->
                        it.summary = input
                    }
                    .inputType(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE)
                    .positiveText(android.R.string.ok)
                    .negativeText(android.R.string.cancel)
                    .show()

            true
        }

        textSizeList = context!!.resources.getTextArray(R.array.pref_list_text_size)

        prefTitleSize = findPreference("prefTitleSize")
        prefTitleSize.summary = textSizeList[0]
        prefTitleSize.setOnPreferenceClickListener {
            showTextSizeSingleChoiceDialog(it)

            true
        }

        prefTitleColor = findPreference("prefTitleColor")
        prefTitleColor.summary = "#${Integer.toHexString(Color.WHITE).toUpperCase()}"
        prefTitleColor.setOnPreferenceClickListener {
            showColorChooserDialog(it)

            true
        }
    }

    private fun initData() {
        widgetId = requireActivity().intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)

        twBox = ObjectBox.boxStore.boxFor(TextWidget::class.java)
        textWidget = twBox.query().equal(TextWidget_.widgetId, widgetId.toLong()).build().findFirst()

        if (textWidget != null) {
            prefTitle.summary = textWidget?.title
            prefTitleSize.summary = textWidget?.titleSize
            prefTitleColor.summary = textWidget?.titleColor
        }
    }

    private fun showTextSizeSingleChoiceDialog(preference: Preference) {
        var selectedIndex = 0
        for ((index, value) in textSizeList.withIndex()) {
            if (value == preference.summary) {
                selectedIndex = index
                break
            }
        }

        MaterialDialog.Builder(context!!)
                .title(preference.title)
                .items(R.array.pref_list_text_size)
                .itemsCallbackSingleChoice(selectedIndex) { _, _, _, text ->
                    preference.summary = text

                    true
                }
                .negativeText(android.R.string.cancel)
                .show()
    }

    private fun showColorChooserDialog(preference: Preference) {
        val dialog = MaterialDialog.Builder(context!!)
                .title(preference.title)
                .customView(R.layout.dialog_color_chooser, false)
                .positiveText(android.R.string.ok)
                .negativeText(android.R.string.cancel)
                .onPositive { dialog, _ ->
                    preference.summary = "#${Integer.toHexString((dialog.customView as ColorChooserView).color).toUpperCase()}"
                }
                .build()
        (dialog.customView as ColorChooserView).setColorARGB(Color.parseColor(preference.summary.toString()))
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
                                titleColor = prefTitleColor.summary.toString()))
                    } else {
                        textWidget?.title = prefTitle.summary.toString()
                        textWidget?.titleSize = prefTitleSize.summary.toString()
                        textWidget?.titleColor = prefTitleColor.summary.toString()
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

        val views = RemoteViews(context!!.packageName, R.layout.widget_text)
        views.setOnClickPendingIntent(R.id.content, pendingIntent)
        views.setTextViewText(R.id.textTitle, prefTitle.summary.toString())
        views.setTextViewTextSize(R.id.textTitle, TypedValue.COMPLEX_UNIT_SP, prefTitleSize.summary.toString().toFloat())
        views.setTextColor(R.id.textTitle, Color.parseColor(prefTitleColor.summary.toString()))

        AppWidgetManager.getInstance(context).updateAppWidget(widgetId, views)

        val resultIntent = Intent()
        resultIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
        activity!!.setResult(Activity.RESULT_OK, intent)
        activity!!.finish()
    }
}
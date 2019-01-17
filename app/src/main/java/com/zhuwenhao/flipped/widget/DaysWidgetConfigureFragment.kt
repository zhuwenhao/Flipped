package com.zhuwenhao.flipped.widget

import android.app.Activity
import android.app.DatePickerDialog
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
import androidx.preference.SwitchPreference
import com.afollestad.materialdialogs.MaterialDialog
import com.zhuwenhao.flipped.R
import com.zhuwenhao.flipped.db.ObjectBox
import com.zhuwenhao.flipped.ext.getDefaultSp
import com.zhuwenhao.flipped.ext.remove
import com.zhuwenhao.flipped.ext.toColorHex
import com.zhuwenhao.flipped.view.ColorChooserView
import io.objectbox.Box
import org.joda.time.DateTime
import org.joda.time.Period
import org.joda.time.PeriodType
import org.joda.time.format.DateTimeFormat

class DaysWidgetConfigureFragment : PreferenceFragmentCompat() {

    private lateinit var prefTitle: Preference
    private lateinit var prefStartDate: Preference
    private lateinit var prefTitleSize: Preference
    private lateinit var prefTitleColor: Preference
    private lateinit var prefDaysColorSameAsTitleColor: SwitchPreference
    private lateinit var prefDaysSize: Preference
    private lateinit var prefDaysColor: Preference
    private lateinit var prefTitleColorSameAsDaysColor: SwitchPreference
    private lateinit var prefAlignment: Preference

    private lateinit var textSizeList: Array<CharSequence>
    private lateinit var textAlignmentList: Array<CharSequence>
    private var textAlignment = 4

    private lateinit var dwBox: Box<DaysWidget>

    private var widgetId = 0
    private var daysWidget: DaysWidget? = null

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.pref_days_widget)
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

        prefStartDate = findPreference("prefStartDate")
        prefStartDate.summary = DateTime.now().toString("yyyy-MM-dd")
        prefStartDate.setOnPreferenceClickListener {
            val dateTime = DateTime.parse(it.summary.toString(), DateTimeFormat.forPattern("yyyy-MM-dd"))
            val dialog = DatePickerDialog(context!!, DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                it.summary = DateTime(year, month + 1, dayOfMonth, 0, 0).toString("yyyy-MM-dd")
            }, dateTime.year, dateTime.monthOfYear - 1, dateTime.dayOfMonth)
            dialog.datePicker.maxDate = System.currentTimeMillis()
            dialog.show()

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
        prefTitleColor.summary = Color.WHITE.toColorHex()
        prefTitleColor.setOnPreferenceClickListener {
            showColorChooserDialog(it)

            true
        }

        prefDaysColorSameAsTitleColor = findPreference("prefDaysColorSameAsTitleColor") as SwitchPreference
        prefDaysColorSameAsTitleColor.setOnPreferenceClickListener {
            val enabled = prefDaysColorSameAsTitleColor.isChecked.not()
            prefDaysColor.isEnabled = enabled
            prefTitleColorSameAsDaysColor.isEnabled = enabled

            if (enabled.not()) {
                prefDaysColor.summary = prefTitleColor.summary
            }

            context!!.getDefaultSp().remove("prefDaysColorSameAsTitleColor")

            true
        }

        prefDaysSize = findPreference("prefDaysSize")
        prefDaysSize.summary = textSizeList[1]
        prefDaysSize.setOnPreferenceClickListener {
            showTextSizeSingleChoiceDialog(it)

            true
        }

        prefDaysColor = findPreference("prefDaysColor")
        prefDaysColor.summary = Color.WHITE.toColorHex()
        prefDaysColor.setOnPreferenceClickListener {
            showColorChooserDialog(it)

            true
        }

        prefTitleColorSameAsDaysColor = findPreference("prefTitleColorSameAsDaysColor") as SwitchPreference
        prefTitleColorSameAsDaysColor.setOnPreferenceClickListener {
            val enabled = prefTitleColorSameAsDaysColor.isChecked.not()
            prefTitleColor.isEnabled = enabled
            prefDaysColorSameAsTitleColor.isEnabled = enabled

            if (enabled.not()) {
                prefTitleColor.summary = prefDaysColor.summary
            }

            context!!.getDefaultSp().remove("prefTitleColorSameAsDaysColor")

            true
        }

        textAlignmentList = context!!.resources.getTextArray(R.array.pref_list_text_alignment)

        prefAlignment = findPreference("prefAlignment")
        prefAlignment.summary = textAlignmentList[textAlignment]
        prefAlignment.setOnPreferenceClickListener {
            var selectedIndex = 0
            for ((index, value) in textAlignmentList.withIndex()) {
                if (value == it.summary) {
                    selectedIndex = index
                    break
                }
            }

            MaterialDialog.Builder(context!!)
                    .title(it.title)
                    .items(R.array.pref_list_text_alignment)
                    .itemsCallbackSingleChoice(selectedIndex) { _, _, which, text ->
                        textAlignment = which
                        it.summary = text

                        true
                    }
                    .negativeText(android.R.string.cancel)
                    .show()

            true
        }
    }

    private fun initData() {
        widgetId = requireActivity().intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)

        dwBox = ObjectBox.boxStore.boxFor(DaysWidget::class.java)
        daysWidget = dwBox.query().equal(DaysWidget_.widgetId, widgetId.toLong()).build().findFirst()

        if (daysWidget != null) {
            prefTitle.summary = daysWidget!!.title
            prefStartDate.summary = daysWidget!!.startDate
            prefTitleSize.summary = daysWidget!!.titleSize
            prefTitleColor.summary = daysWidget!!.titleColor
            prefDaysSize.summary = daysWidget!!.daysSize
            prefDaysColor.summary = daysWidget!!.daysColor
            when (daysWidget!!.colorSameAs) {
                1 -> {
                    prefDaysColorSameAsTitleColor.isChecked = true
                    prefDaysColor.isEnabled = false
                    prefTitleColorSameAsDaysColor.isEnabled = false
                }
                2 -> {
                    prefTitleColorSameAsDaysColor.isChecked = true
                    prefTitleColor.isEnabled = false
                    prefDaysColorSameAsTitleColor.isEnabled = false
                }
            }
            prefAlignment.summary = textAlignmentList[daysWidget!!.textAlignment]
            textAlignment = daysWidget!!.textAlignment
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
                    preference.summary = (dialog.customView as ColorChooserView).color.toColorHex()

                    if (preference == prefTitleColor && prefDaysColorSameAsTitleColor.isChecked) {
                        prefDaysColor.summary = preference.summary
                    } else if (preference == prefDaysColor && prefTitleColorSameAsDaysColor.isChecked) {
                        prefTitleColor.summary = preference.summary
                    }
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
                    val colorSameAs = when {
                        prefDaysColorSameAsTitleColor.isChecked -> 1
                        prefTitleColorSameAsDaysColor.isChecked -> 2
                        else -> 0
                    }

                    if (daysWidget == null) {
                        dwBox.put(DaysWidget(widgetId = widgetId,
                                title = prefTitle.summary.toString(),
                                startDate = prefStartDate.summary.toString(),
                                titleSize = prefTitleSize.summary.toString(),
                                titleColor = prefTitleColor.summary.toString(),
                                daysSize = prefDaysSize.summary.toString(),
                                daysColor = prefDaysColor.summary.toString(),
                                colorSameAs = colorSameAs,
                                textAlignment = textAlignment))
                    } else {
                        daysWidget?.title = prefTitle.summary.toString()
                        daysWidget?.startDate = prefStartDate.summary.toString()
                        daysWidget?.titleSize = prefTitleSize.summary.toString()
                        daysWidget?.titleColor = prefTitleColor.summary.toString()
                        daysWidget?.daysSize = prefDaysSize.summary.toString()
                        daysWidget?.daysColor = prefDaysColor.summary.toString()
                        daysWidget?.colorSameAs = colorSameAs
                        daysWidget?.textAlignment = textAlignment
                        dwBox.put(daysWidget!!)
                    }
                    updateWidgetUI()
                }
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun updateWidgetUI() {
        val intent = Intent(context, DaysWidgetConfigureActivity::class.java)
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
        val pendingIntent = PendingIntent.getActivity(context, widgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val views = RemoteViews(context!!.packageName, when (textAlignment) {
            0 -> R.layout.widget_days_top_left
            1 -> R.layout.widget_days_top_right
            2 -> R.layout.widget_days_bottom_left
            3 -> R.layout.widget_days_bottom_right
            4 -> R.layout.widget_days_center
            5 -> R.layout.widget_days_center_top
            6 -> R.layout.widget_days_center_bottom
            7 -> R.layout.widget_days_center_left
            8 -> R.layout.widget_days_center_right
            else -> R.layout.widget_days_center
        })
        views.setOnClickPendingIntent(R.id.content, pendingIntent)
        views.setTextViewText(R.id.textTitle, prefTitle.summary.toString())
        views.setTextViewTextSize(R.id.textTitle, TypedValue.COMPLEX_UNIT_SP, prefTitleSize.summary.toString().toFloat())
        views.setTextColor(R.id.textTitle, Color.parseColor(prefTitleColor.summary.toString()))
        views.setTextViewText(R.id.textDays, context!!.getString(R.string.days_widget_days, Period(DateTime.parse(prefStartDate.summary.toString(), DateTimeFormat.forPattern("yyyy-MM-dd")), DateTime.now(), PeriodType.days()).days + 1))
        views.setTextViewTextSize(R.id.textDays, TypedValue.COMPLEX_UNIT_SP, prefDaysSize.summary.toString().toFloat())
        views.setTextColor(R.id.textDays, Color.parseColor(prefDaysColor.summary.toString()))

        AppWidgetManager.getInstance(context).updateAppWidget(widgetId, views)

        val resultIntent = Intent()
        resultIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
        activity!!.setResult(Activity.RESULT_OK, intent)
        activity!!.finish()
    }
}
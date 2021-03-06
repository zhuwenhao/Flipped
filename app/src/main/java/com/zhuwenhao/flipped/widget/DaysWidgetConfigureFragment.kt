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
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.afollestad.materialdialogs.input.input
import com.afollestad.materialdialogs.list.listItems
import com.zhuwenhao.flipped.R
import com.zhuwenhao.flipped.db.ObjectBox
import com.zhuwenhao.flipped.ext.toColorHex
import com.zhuwenhao.flipped.view.ColorChooserView
import io.objectbox.Box
import org.joda.time.DateTime
import org.joda.time.Period
import org.joda.time.PeriodType
import org.joda.time.format.DateTimeFormat

class DaysWidgetConfigureFragment : PreferenceFragmentCompat() {

    private lateinit var prefTitle: Preference
    private lateinit var prefCountdown: SwitchPreference
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

        prefCountdown = findPreference("prefCountdown")!!
        prefCountdown.isChecked = false
        prefCountdown.setOnPreferenceClickListener {
            prefStartDate.title = getString(if (prefCountdown.isChecked) R.string.end_date else R.string.start_date)
            prefStartDate.summary = null

            true
        }

        prefStartDate = findPreference("prefStartDate")!!
        prefStartDate.summary = DateTime.now().toString("yyyy-MM-dd")
        prefStartDate.setOnPreferenceClickListener {
            val dateTime = if (it.summary == null) {
                DateTime.now()
            } else {
                DateTime.parse(it.summary.toString(), DateTimeFormat.forPattern("yyyy-MM-dd"))
            }
            val dialog = DatePickerDialog(requireContext(), DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                it.summary = DateTime(year, month + 1, dayOfMonth, 0, 0).toString("yyyy-MM-dd")
            }, dateTime.year, dateTime.monthOfYear - 1, dateTime.dayOfMonth)
            if (prefCountdown.isChecked) {
                dialog.datePicker.minDate = System.currentTimeMillis()
            } else {
                dialog.datePicker.maxDate = System.currentTimeMillis()
            }
            dialog.show()

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

        prefDaysColorSameAsTitleColor = findPreference("prefDaysColorSameAsTitleColor")!!
        prefDaysColorSameAsTitleColor.isChecked = false
        prefDaysColorSameAsTitleColor.setOnPreferenceClickListener {
            val enabled = prefDaysColorSameAsTitleColor.isChecked.not()
            prefDaysColor.isEnabled = enabled
            prefTitleColorSameAsDaysColor.isEnabled = enabled

            if (enabled.not()) {
                prefDaysColor.summary = prefTitleColor.summary
            }

            true
        }

        prefDaysSize = findPreference("prefDaysSize")!!
        prefDaysSize.summary = textSizeList[1]
        prefDaysSize.setOnPreferenceClickListener {
            showTextSizeSingleChoiceDialog(it)

            true
        }

        prefDaysColor = findPreference("prefDaysColor")!!
        prefDaysColor.summary = Color.WHITE.toColorHex()
        prefDaysColor.setOnPreferenceClickListener {
            showColorChooserDialog(it)

            true
        }

        prefTitleColorSameAsDaysColor = findPreference("prefTitleColorSameAsDaysColor")!!
        prefTitleColorSameAsDaysColor.isChecked = false
        prefTitleColorSameAsDaysColor.setOnPreferenceClickListener {
            val enabled = prefTitleColorSameAsDaysColor.isChecked.not()
            prefTitleColor.isEnabled = enabled
            prefDaysColorSameAsTitleColor.isEnabled = enabled

            if (enabled.not()) {
                prefTitleColor.summary = prefDaysColor.summary
            }

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

        dwBox = ObjectBox.boxStore.boxFor(DaysWidget::class.java)
        daysWidget = dwBox.query().equal(DaysWidget_.widgetId, widgetId.toLong()).build().findFirst()

        if (daysWidget != null) {
            prefTitle.summary = daysWidget!!.title
            prefCountdown.isChecked = daysWidget!!.countdown
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

                    if (preference == prefTitleColor && prefDaysColorSameAsTitleColor.isChecked) {
                        prefDaysColor.summary = preference.summary
                    } else if (preference == prefDaysColor && prefTitleColorSameAsDaysColor.isChecked) {
                        prefTitleColor.summary = preference.summary
                    }
                }
                .negativeButton(android.R.string.cancel)
        (dialog.getCustomView() as ColorChooserView).setColorARGB(Color.parseColor(preference.summary.toString()))
        dialog.show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> activity?.onBackPressed()
            R.id.menu_apply -> {
                when {
                    prefTitle.summary == null -> {
                        Toast.makeText(context, R.string.widget_title_check_hint, Toast.LENGTH_SHORT).show()
                    }
                    prefStartDate.summary == null -> {
                        Toast.makeText(context, R.string.widget_date_check_hint, Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        val colorSameAs = when {
                            prefDaysColorSameAsTitleColor.isChecked -> 1
                            prefTitleColorSameAsDaysColor.isChecked -> 2
                            else -> 0
                        }

                        if (daysWidget == null) {
                            dwBox.put(DaysWidget(widgetId = widgetId,
                                    title = prefTitle.summary.toString(),
                                    countdown = prefCountdown.isChecked,
                                    startDate = prefStartDate.summary.toString(),
                                    titleSize = prefTitleSize.summary.toString(),
                                    titleColor = prefTitleColor.summary.toString(),
                                    daysSize = prefDaysSize.summary.toString(),
                                    daysColor = prefDaysColor.summary.toString(),
                                    colorSameAs = colorSameAs,
                                    textAlignment = textAlignment))
                        } else {
                            daysWidget?.title = prefTitle.summary.toString()
                            daysWidget?.countdown = prefCountdown.isChecked
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

        val views = RemoteViews(requireContext().packageName, when (textAlignment) {
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
        views.setTextViewText(R.id.textDays, if (prefCountdown.isChecked) {
            if (prefStartDate.summary == DateTime.now().toString("yyyy-MM-dd")) {
                requireContext().getString(R.string.days_widget_days_today)
            } else {
                requireContext().getString(R.string.days_widget_days_left, Period(DateTime.now(), DateTime.parse(prefStartDate.summary.toString(), DateTimeFormat.forPattern("yyyy-MM-dd")), PeriodType.days()).days + 1)
            }
        } else {
            requireContext().getString(R.string.days_widget_days, Period(DateTime.parse(prefStartDate.summary.toString(), DateTimeFormat.forPattern("yyyy-MM-dd")), DateTime.now(), PeriodType.days()).days + 1)
        })
        views.setTextViewTextSize(R.id.textDays, TypedValue.COMPLEX_UNIT_SP, prefDaysSize.summary.toString().toFloat())
        views.setTextColor(R.id.textDays, Color.parseColor(prefDaysColor.summary.toString()))

        AppWidgetManager.getInstance(context).updateAppWidget(widgetId, views)

        val resultIntent = Intent()
        resultIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
        requireActivity().setResult(Activity.RESULT_OK, intent)
        requireActivity().finish()
    }
}
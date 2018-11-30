package com.zhuwenhao.flipped.bandwagon.activity

import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.text.Editable
import android.text.TextWatcher
import android.widget.AutoCompleteTextView
import com.afollestad.materialdialogs.DialogAction
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.internal.MDButton
import com.chad.library.adapter.base.callback.ItemDragAndSwipeCallback
import com.chad.library.adapter.base.listener.OnItemDragListener
import com.chad.library.adapter.base.listener.OnItemSwipeListener
import com.github.magiepooh.recycleritemdecoration.ItemDecorations
import com.zhuwenhao.flipped.R
import com.zhuwenhao.flipped.bandwagon.adapter.BandwagonAdapter
import com.zhuwenhao.flipped.bandwagon.entity.Bandwagon
import com.zhuwenhao.flipped.base.BaseSubActivity
import com.zhuwenhao.flipped.util.DatabaseUtils
import com.zhuwenhao.flipped.util.DisplayUtils
import com.zhuwenhao.flipped.util.ImageUtils
import kotlinx.android.synthetic.main.activity_bandwagon.*
import kotlinx.android.synthetic.main.layout_toolbar.*

class BandwagonActivity : BaseSubActivity(), TextWatcher {

    private var bandwagonList: MutableList<Bandwagon> = ArrayList()
    private lateinit var adapter: BandwagonAdapter

    private lateinit var dialogTextTitle: AutoCompleteTextView
    private lateinit var dialogTextVeId: AutoCompleteTextView
    private lateinit var dialogTextApiKey: AutoCompleteTextView
    private lateinit var dialogPositiveBtn: MDButton

    override fun provideLayoutId(): Int {
        return R.layout.activity_bandwagon
    }

    override fun initView() {
        setSupportActionBar(toolbar)

        adapter = BandwagonAdapter(bandwagonList)
        adapter.setOnItemClickListener { _, _, position ->
            val intent = Intent(this, BandwagonDetailActivity::class.java)
            intent.putExtra("bandwagon", bandwagonList[position])
            startActivity(intent)
        }
        adapter.setOnItemChildClickListener { _, _, position ->
            showEditDialog(true, position)
        }

        val itemDragAndSwipeCallback = ItemDragAndSwipeCallback(adapter)
        itemDragAndSwipeCallback.setSwipeMoveFlags(ItemTouchHelper.START or ItemTouchHelper.END)
        itemDragAndSwipeCallback.setDragMoveFlags(ItemTouchHelper.UP or ItemTouchHelper.DOWN)
        val itemTouchHelper = ItemTouchHelper(itemDragAndSwipeCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
        adapter.enableSwipeItem()
        adapter.setOnItemSwipeListener(object : OnItemSwipeListener {
            override fun onItemSwipeStart(viewHolder: RecyclerView.ViewHolder?, pos: Int) {

            }

            override fun clearView(viewHolder: RecyclerView.ViewHolder?, pos: Int) {

            }

            override fun onItemSwiped(viewHolder: RecyclerView.ViewHolder?, pos: Int) {

            }

            override fun onItemSwipeMoving(canvas: Canvas, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, isCurrentlyActive: Boolean) {
                canvas.drawColor(Color.parseColor("#D83025"))

                val itemView = viewHolder.itemView
                val icon = ImageUtils.drawableToBitmap(ContextCompat.getDrawable(mContext, R.drawable.ic_delete_white)!!)
                val margin = DisplayUtils.dpToPx(mContext.applicationContext, 24F)
                canvas.drawBitmap(icon, if (dX > 0) itemView.left.toFloat() + margin else -dX - margin - icon.width, (itemView.bottom.toFloat() - itemView.top.toFloat() - icon.height.toFloat()) / 2, null)
            }
        })
        adapter.enableDragItem(itemTouchHelper)
        adapter.setOnItemDragListener(object : OnItemDragListener {
            override fun onItemDragMoving(source: RecyclerView.ViewHolder?, from: Int, target: RecyclerView.ViewHolder?, to: Int) {

            }

            override fun onItemDragStart(viewHolder: RecyclerView.ViewHolder?, pos: Int) {

            }

            override fun onItemDragEnd(viewHolder: RecyclerView.ViewHolder?, pos: Int) {

            }
        })

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.addItemDecoration(ItemDecorations.vertical(this).type(0, R.drawable.item_decoration_h_1).create())
        recyclerView.adapter = adapter

        fabAdd.setOnClickListener {
            showEditDialog(false, -1)
        }
    }

    override fun initData() {
        bandwagonList.clear()
        bandwagonList.addAll(DatabaseUtils.getBandwagonList(applicationContext))
        adapter.notifyDataSetChanged()
    }

    private fun showEditDialog(isEdit: Boolean, position: Int) {
        val dialog = MaterialDialog.Builder(this)
                .title(if (isEdit) R.string.edit_bandwagon else R.string.add_bandwagon)
                .customView(R.layout.dialog_bandwagon_add, true)
                .positiveText(android.R.string.ok)
                .negativeText(android.R.string.cancel)
                .onPositive { _, _ ->
                    if (isEdit) {
                        DatabaseUtils.updateBandwagon(applicationContext, bandwagonList[position].id, dialogTextTitle.text.toString(), dialogTextVeId.text.toString(), dialogTextApiKey.text.toString())
                    } else {
                        DatabaseUtils.addBandwagon(applicationContext, dialogTextTitle.text.toString(), dialogTextVeId.text.toString(), dialogTextApiKey.text.toString())
                    }
                }
                .canceledOnTouchOutside(false)
                .build()

        dialogPositiveBtn = dialog.getActionButton(DialogAction.POSITIVE)
        dialogTextTitle = dialog.customView!!.findViewById(R.id.textTitle)
        dialogTextTitle.addTextChangedListener(this)
        dialogTextVeId = dialog.customView!!.findViewById(R.id.textVeId)
        dialogTextVeId.addTextChangedListener(this)
        dialogTextApiKey = dialog.customView!!.findViewById(R.id.textApiKey)
        dialogTextApiKey.addTextChangedListener(this)
        if (isEdit) {
            val bandwagon = adapter.data[position] as Bandwagon
            dialogTextTitle.setText(bandwagon.title)
            dialogTextVeId.setText(bandwagon.veId)
            dialogTextApiKey.setText(bandwagon.apiKey)
        }
        dialog.show()
        dialogPositiveBtn.isEnabled = isEdit
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        dialogPositiveBtn.isEnabled = dialogTextTitle.text.toString().trim().isNotEmpty() && dialogTextVeId.text.toString().trim().isNotEmpty() && dialogTextApiKey.text.toString().trim().isNotEmpty()
    }

    override fun afterTextChanged(s: Editable?) {

    }
}
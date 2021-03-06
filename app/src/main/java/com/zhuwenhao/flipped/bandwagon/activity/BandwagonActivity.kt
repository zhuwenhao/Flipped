package com.zhuwenhao.flipped.bandwagon.activity

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.InputMethodManager
import android.widget.AutoCompleteTextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.WhichButton
import com.afollestad.materialdialogs.actions.getActionButton
import com.afollestad.materialdialogs.callbacks.onShow
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.afollestad.materialdialogs.internal.button.DialogActionButton
import com.chad.library.adapter.base.listener.OnItemDragListener
import com.chad.library.adapter.base.listener.OnItemSwipeListener
import com.github.magiepooh.recycleritemdecoration.ItemDecorations
import com.zhuwenhao.flipped.R
import com.zhuwenhao.flipped.bandwagon.Bandwagon
import com.zhuwenhao.flipped.bandwagon.Bandwagon_
import com.zhuwenhao.flipped.bandwagon.adapter.BandwagonAdapter
import com.zhuwenhao.flipped.base.BaseSubActivity
import com.zhuwenhao.flipped.db.ObjectBox
import com.zhuwenhao.flipped.ext.dpToPx
import com.zhuwenhao.flipped.ext.toBitmap
import io.objectbox.Box
import kotlinx.android.synthetic.main.activity_bandwagon.*
import kotlinx.android.synthetic.main.layout_toolbar.*

class BandwagonActivity : BaseSubActivity(), TextWatcher {

    private lateinit var adapter: BandwagonAdapter

    private lateinit var bBox: Box<Bandwagon>

    private lateinit var dialogTextTitle: AutoCompleteTextView
    private lateinit var dialogTextVeId: AutoCompleteTextView
    private lateinit var dialogTextApiKey: AutoCompleteTextView
    private lateinit var dialogPositiveBtn: DialogActionButton

    override fun provideLayoutId(): Int {
        return R.layout.activity_bandwagon
    }

    override fun initView() {
        setSupportActionBar(toolbar)

        adapter = BandwagonAdapter(ArrayList())
        adapter.setOnItemClickListener { _, _, position ->
            val intent = Intent(this, BandwagonDetailActivity::class.java)
            intent.putExtra("bandwagon", adapter.data[position])
            startActivity(intent)
        }
        adapter.addChildClickViewIds(R.id.imgEdit)
        adapter.setOnItemChildClickListener { _, _, position ->
            showEditDialog(true, position)
        }

        adapter.draggableModule.isSwipeEnabled = true
        adapter.draggableModule.setOnItemSwipeListener(object : OnItemSwipeListener {
            override fun onItemSwipeStart(viewHolder: RecyclerView.ViewHolder?, pos: Int) {

            }

            override fun clearView(viewHolder: RecyclerView.ViewHolder?, pos: Int) {

            }

            override fun onItemSwiped(viewHolder: RecyclerView.ViewHolder?, pos: Int) {

            }

            override fun onItemSwipeMoving(canvas: Canvas, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, isCurrentlyActive: Boolean) {
                canvas.drawColor(Color.parseColor("#D83025"))

                val itemView = viewHolder.itemView
                val icon = ContextCompat.getDrawable(mContext, R.drawable.ic_delete_white)!!.toBitmap()
                val margin = dpToPx(24F)
                canvas.drawBitmap(icon, if (dX > 0) itemView.left.toFloat() + margin else -dX - margin - icon.width, (itemView.bottom.toFloat() - itemView.top.toFloat() - icon.height.toFloat()) / 2, null)
            }
        })
        adapter.draggableModule.isDragEnabled = true
        adapter.draggableModule.setOnItemDragListener(object : OnItemDragListener {
            override fun onItemDragMoving(source: RecyclerView.ViewHolder?, from: Int, target: RecyclerView.ViewHolder?, to: Int) {
                val fromB = adapter.data[from]
                val toB = adapter.data[to]

                val fromBUserOrder = fromB.userOrder
                fromB.userOrder = toB.userOrder
                toB.userOrder = fromBUserOrder

                bBox.put(fromB)
                bBox.put(toB)
            }

            override fun onItemDragStart(viewHolder: RecyclerView.ViewHolder?, pos: Int) {

            }

            override fun onItemDragEnd(viewHolder: RecyclerView.ViewHolder?, pos: Int) {

            }
        })
        adapter.draggableModule.itemTouchHelperCallback.setSwipeMoveFlags(ItemTouchHelper.START or ItemTouchHelper.END)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.addItemDecoration(ItemDecorations.vertical(this).type(0, R.drawable.item_decoration_h_1).create())
        recyclerView.adapter = adapter

        fabAdd.setOnClickListener {
            showEditDialog(false, -1)
        }
    }

    override fun initData() {
        bBox = ObjectBox.boxStore.boxFor(Bandwagon::class.java)

        adapter.setList(bBox.query().order(Bandwagon_.userOrder).build().find())
        adapter.notifyDataSetChanged()
    }

    private fun showEditDialog(isEdit: Boolean, position: Int) {
        val dialog = MaterialDialog(this)
                .title(if (isEdit) R.string.edit_bandwagon else R.string.add_bandwagon)
                .customView(R.layout.dialog_bandwagon_add, scrollable = true)
                .positiveButton(android.R.string.ok) {
                    if (isEdit) {
                        val bandwagon = adapter.data[position]
                        bandwagon.title = dialogTextTitle.text.toString()
                        bandwagon.veId = dialogTextVeId.text.toString()
                        bandwagon.apiKey = dialogTextApiKey.text.toString()
                        bBox.put(bandwagon)
                    } else {
                        val lastUserOrder = bBox.query().build().property(Bandwagon_.userOrder).max().toInt()
                        bBox.put(Bandwagon(title = dialogTextTitle.text.toString(),
                                veId = dialogTextVeId.text.toString(),
                                apiKey = dialogTextApiKey.text.toString(),
                                userOrder = lastUserOrder + 1))
                    }
                }
                .negativeButton(android.R.string.cancel)
                .cancelOnTouchOutside(false)
                .onShow {
                    dialogTextTitle.requestFocus()
                    dialogTextTitle.setSelection(dialogTextTitle.length())
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.showSoftInput(dialogTextTitle, InputMethodManager.SHOW_IMPLICIT)
                }

        dialogPositiveBtn = dialog.getActionButton(WhichButton.POSITIVE)
        dialogTextTitle = dialog.getCustomView().findViewById(R.id.textTitle)
        dialogTextTitle.addTextChangedListener(this)
        dialogTextVeId = dialog.getCustomView().findViewById(R.id.textVeId)
        dialogTextVeId.addTextChangedListener(this)
        dialogTextApiKey = dialog.getCustomView().findViewById(R.id.textApiKey)
        dialogTextApiKey.addTextChangedListener(this)
        if (isEdit) {
            val bandwagon = adapter.data[position]
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

    override fun isSwipeBackEdgeOnly() = true
}
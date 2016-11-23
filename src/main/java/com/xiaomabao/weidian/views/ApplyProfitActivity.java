package com.xiaomabao.weidian.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import com.xiaomabao.weidian.AppContext;
import com.xiaomabao.weidian.R;
import com.xiaomabao.weidian.presenters.ApplyProfitPresenter;
import com.xiaomabao.weidian.services.ProfitService;
import com.xiaomabao.weidian.ui.InputFilterMinMax;
import com.xiaomabao.weidian.util.XmbPopubWindow;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class ApplyProfitActivity extends AppCompatActivity {

    @BindString(R.string.apply_profit) String toolbarText;

    @BindView(R.id.toolbar_title)
    TextView toolBarTextView;
    @BindView(R.id.available_balance_profit)
    TextView availableBalanceProfitTextView;
    @BindView(R.id.drawing_account)
    TextView drawingAccountTextView;
    @BindView(R.id.deposit_bank)
    TextView depositBankTextView;
    @BindView(R.id.card_no)
    TextView cardNoTextView;
    @BindView(R.id.edit_profit)
    EditText editText;

    @OnClick(R.id.back) void back() {
        finish();
    }
    @OnClick(R.id.confirm) void submit() {
        String extract_profit = editText.getText().toString();
        if (extract_profit.equals("")){
            XmbPopubWindow.showAlert(this,"请输入提现金额~");
        }else{
            Double extract_profit_double = Double.parseDouble(extract_profit);
            if(extract_profit_double < 10.0){
                XmbPopubWindow.showAlert(this,"申请提现金额不得少于10.00元哦~");
            }else{
                XmbPopubWindow.showTranparentLoading(this);
                mPresenter.apply_withdraw(ProfitService.gen_apply_withdraw_params(AppContext.getToken(this),extract_profit_double));
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_profit);
        ButterKnife.bind(this);
        setView();
        initApi();
    }


    ProfitService mService;
    ApplyProfitPresenter mPresenter;

    private void initApi(){
        mService = new ProfitService();
        mPresenter = new ApplyProfitPresenter(this,mService);
    }

    protected void setView(){
        String bank = AppContext.getCardDepositBank(this)+AppContext.getCardBranchBank(this);
        toolBarTextView.setText(toolbarText);
        availableBalanceProfitTextView.setText(getIntent().getStringExtra("balance"));
        drawingAccountTextView.setText(AppContext.getCardRealName(this));
        depositBankTextView.setText(bank);
        cardNoTextView.setText(AppContext.getCardNo(this));
        editText.setFilters(new InputFilter[]{ new InputFilterMinMax("0.00", getIntent().getStringExtra("balance"))});
        editText.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable edt) {
                String temp = edt.toString();
                int posDot = temp.indexOf(".");
                if (posDot <= 0) return;
                if (temp.length() - posDot - 1 > 2){
                    edt.delete(posDot + 3, posDot + 4);
                }
            }
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}
        });
    }

    public void withdrawSuccessResponse(String apply_money){
        Intent intent = new Intent(ApplyProfitActivity.this,ApplyWithDrawSuccessActivity.class);
        intent.putExtra("money",apply_money);
        startActivity(intent);
        finish();
    }

}

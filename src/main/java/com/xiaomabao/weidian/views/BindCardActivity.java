package com.xiaomabao.weidian.views;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.xiaomabao.weidian.util.WheelView;
import com.xiaomabao.weidian.AppContext;
import com.xiaomabao.weidian.R;
import com.xiaomabao.weidian.models.City;
import com.xiaomabao.weidian.models.Country;
import com.xiaomabao.weidian.models.Province;
import com.xiaomabao.weidian.presenters.BindCardPresenter;
import com.xiaomabao.weidian.services.ProfitService;
import com.xiaomabao.weidian.util.CommonUtil;
import com.xiaomabao.weidian.util.XmbDB;
import com.xiaomabao.weidian.util.XmbPopubWindow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BindCardActivity extends AppCompatActivity {

    @BindString(R.string.bind_card) String toolbarTitle;
    @BindView(R.id.toolbar_title)TextView toolbarTextView;
    @BindView(R.id.wheelView_container) View wheelView_container;
    @BindView(R.id.wheelView)
    WheelView wheelView;
    @BindView(R.id.bind_account_hint)
    EditText accountEditText;
    @BindView(R.id.bind_branch_text)
    EditText branchEditText;
    @BindView(R.id.bind_card_no)
    EditText cardNoEditText;
    @BindView(R.id.bind_mobile_phone)
    EditText phoneEditText;
    @BindView(R.id.bind_deposit_bank)
    TextView depositBankTextView;
    @BindView(R.id.province_text)
    TextView provinceTextView;
    @BindView(R.id.city_text)
    TextView cityTextView;
    @BindView(R.id.district_text)
    TextView districtTextView;

    private int current_type = 0;
    private String deposit_bank = "";
    private String province_name = "";
    private String city_name = "";
    private String district_name = "";
    private String province_id = "",city_id = "";
    private static final String[] BANKS = new String[]{"中国银行", "中国工商银行", "中国建设银行", "中国农业银行", "招商银行", "交通银行"};
    private ArrayList<String> provinceList = new ArrayList<>();
    private ArrayList<String> cityList = new ArrayList<>();
    private ArrayList<String> regionList = new ArrayList<>();
    private List<Province> provinces = new ArrayList<>();
    private List<City> cities = new ArrayList<>();
    private List<Country> districts = new ArrayList<>();

    @OnClick(R.id.back) void back() {
        finish();
    }
    @OnClick(R.id.cancel) void cancelHideWheelView() {
        wheelView_container.setVisibility(View.GONE);
    }
    @OnClick(R.id.bind_card)void bind_card(){
        String account_name = accountEditText.getText().toString();
        String branch_bank = branchEditText.getText().toString();
        String card_no = cardNoEditText.getText().toString();
        String mobile_phone = phoneEditText.getText().toString();
        if(!CommonUtil.nEmptyStringValid(account_name)){
            XmbPopubWindow.showAlert(this,"请输入您的姓名~");
            return;
        }
        if(!CommonUtil.nEmptyStringValid(province_name)){
            XmbPopubWindow.showAlert(this,"请选择省份~");
            return;
        }
        if(!CommonUtil.nEmptyStringValid(city_name)){
            XmbPopubWindow.showAlert(this,"请选择市~");
            return;
        }
        if(!CommonUtil.nEmptyStringValid(district_name)){
            XmbPopubWindow.showAlert(this,"请选择区县~");
            return;
        }
        if(!CommonUtil.nEmptyStringValid(deposit_bank)){
            XmbPopubWindow.showAlert(this,"请选择开户行~");
            return;
        }
        if(!CommonUtil.nEmptyStringValid(branch_bank)){
            XmbPopubWindow.showAlert(this,"请输入开户行支行~");
            return;
        }
        if(!CommonUtil.nEmptyStringValid(card_no)){
            XmbPopubWindow.showAlert(this,"请输入银行卡卡号~");
            return;
        }
        if(!CommonUtil.nEmptyStringValid(mobile_phone)){
            XmbPopubWindow.showAlert(this,"请输入您银行卡绑定的手机号码~");
            return;
        }
        if(!CommonUtil.isMobilePhone(mobile_phone)){
            XmbPopubWindow.showAlert(this,"手机号码格式错误~");
            return;
        }
        XmbPopubWindow.showTranparentLoading(this);
        HashMap<String,String> hashMap = ProfitService.gen_bind_card_params(AppContext.getToken(this),account_name,province_name,city_name,district_name,deposit_bank,branch_bank,card_no,mobile_phone);
        mPresenter.bind_card(hashMap);
    }
    @OnClick(R.id.finish) void finishHideWheelView() {
        if (current_type == 0){
            deposit_bank = BANKS[wheelView.getSeletedIndex()];
            depositBankTextView.setText(BANKS[wheelView.getSeletedIndex()]);
        }else if(current_type == 1){
            if (!provinces.get(wheelView.getSeletedIndex()).getCode().equals(province_id)) {
                province_name = provinces.get(wheelView.getSeletedIndex()).getName();
                provinceTextView.setText(provinces.get(wheelView.getSeletedIndex()).getName());
                cityTextView.setText("");
                districtTextView.setText("");
                province_id = provinces.get(wheelView.getSeletedIndex()).getCode();
                city_name = "";
                district_name = "";
            }
        }else if(current_type == 2){
            if (!cities.get(wheelView.getSeletedIndex()).getCode().equals(city_id)) {
                city_name = cities.get(wheelView.getSeletedIndex()).getName();
                cityTextView.setText(cities.get(wheelView.getSeletedIndex()).getName());
                city_id = cities.get(wheelView.getSeletedIndex()).getCode();
                districtTextView.setText("");
                district_name = "";
            }
        }else if(current_type == 3){
            district_name = districts.get(wheelView.getSeletedIndex()).getName();
            districtTextView.setText(districts.get(wheelView.getSeletedIndex()).getName());
        }
        wheelView_container.setVisibility(View.GONE);
    }

    protected void  showWheelView(int type) {
        current_type = type;
        setWheelView();
        wheelView_container.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_card);
        ButterKnife.bind(this);
        setView();
        initApi();
    }

    protected void setView(){
        toolbarTextView.setText(toolbarTitle);
        accountEditText.setOnClickListener((view)->cancelHideWheelView());
        branchEditText.setOnClickListener((view)->cancelHideWheelView());
        phoneEditText.setOnClickListener((view)->cancelHideWheelView());

        depositBankTextView.setOnClickListener((view)->showWheelView(0));
        provinceTextView.setOnClickListener((view)->showWheelView(1));
        cityTextView.setOnClickListener((view)->showWheelView(2));
        districtTextView.setOnClickListener((view)->showWheelView(3));
    }

    protected void setWheelView(){
        wheelView.setOffset(2);

        if (current_type == 0){
            wheelView.setItems(Arrays.asList(BANKS));
        }else{
            if(current_type == 1){
                provinces.clear();
                provinceList.clear();
                provinces = XmbDB.getInstance(this, AppContext.DBPath).loadProvinces();
                for(int i = 0; i < provinces.size();i++){
                    provinceList.add(provinces.get(i).getName());
                }
                wheelView.setItems(provinceList);
            }else if(current_type == 2){
                if(province_name.equals("")){
                    XmbPopubWindow.showAlert(this,"请选择省份");
                    return;
                }
                cities.clear();
                cityList.clear();
                cities = XmbDB.getInstance(this, AppContext.DBPath).loadCities(province_id);
                for(int i = 0; i < cities.size();i++){
                    cityList.add(cities.get(i).getName());
                }
                wheelView.setItems(cityList);
            }else if(current_type == 3){
                if(city_id.equals("")){
                    XmbPopubWindow.showAlert(this,"请选择城市");
                    return;
                }
                districts.clear();
                regionList.clear();
                districts = XmbDB.getInstance(this, AppContext.DBPath).loadCountries(city_id);
                districts.remove(0);
                for(int i = 0; i < districts.size();i++){
                    regionList.add(districts.get(i).getName());
                }
                wheelView.setItems(regionList);
            }
        }
        wheelView.setSeletion(0);

        wheelView.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
            @Override
            public void onSelected(int selectedIndex, String item) {
                Log.d("selected", "selectedIndex: " + selectedIndex + ", item: " + item);
            }
        });
    }



    public void bindCardCallback(){
        setResult(RESULT_OK);
        finish();
    }

    ProfitService mService;
    BindCardPresenter mPresenter;

    protected void initApi(){
        mService = new ProfitService();
        mPresenter = new BindCardPresenter(this,mService);
    }
}

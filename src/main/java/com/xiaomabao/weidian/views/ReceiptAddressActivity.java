package com.xiaomabao.weidian.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.xiaomabao.weidian.defines.Const;
import com.xiaomabao.weidian.models.ApplyShopCode;
import com.xiaomabao.weidian.util.WheelView;
import com.xiaomabao.weidian.AppContext;
import com.xiaomabao.weidian.R;
import com.xiaomabao.weidian.models.City;
import com.xiaomabao.weidian.models.Country;
import com.xiaomabao.weidian.models.Province;
import com.xiaomabao.weidian.presenters.ReceiptAddressPresenter;
import com.xiaomabao.weidian.services.UserService;
import com.xiaomabao.weidian.util.CommonUtil;
import com.xiaomabao.weidian.util.InputSoftUtil;
import com.xiaomabao.weidian.util.XmbDB;
import com.xiaomabao.weidian.util.XmbPopubWindow;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ReceiptAddressActivity extends AppCompatActivity {

    @BindString(R.string.add_address)
    String toolbarTitle;

    private String province_name = "";
    private String city_name = "";
    private String district_name = "";
    private String province_id = "", city_id = "";
    private int current_type = 1;

    private ArrayList<String> provinceList = new ArrayList<>();
    private ArrayList<String> cityList = new ArrayList<>();
    private ArrayList<String> regionList = new ArrayList<>();
    private List<Province> provinces = new ArrayList<>();
    private List<City> cities = new ArrayList<>();
    private List<Country> districts = new ArrayList<>();

    @BindView(R.id.wheelView_container)
    View wheelView_container;
    @BindView(R.id.wheelView)
    WheelView wheelView;
    @BindView(R.id.province_text)
    TextView provinceTextView;
    @BindView(R.id.city_text)
    TextView cityTextView;
    @BindView(R.id.district_text)
    TextView districtTextView;

    @BindView(R.id.receipt_address_user)
    TextView receipt_address_user;
    @BindView(R.id.receipt_address_phone)
    TextView receipt_address_phone;
    @BindView(R.id.receipt_address_address)
    TextView receipt_address_address;
    @BindView(R.id.receipt_phone_code)
    TextView receipt_phone_code;

    @BindView(R.id.toolbar_title)
    TextView toolBarTitleTextView;


    @OnClick(R.id.back)
    void back() {
        finish();
    }

    @OnClick(R.id.get_phone_code)
    void get_phone_code() {
        String phone = receipt_address_phone.getText().toString();
        if (!CommonUtil.isMobilePhone(phone)) {
            XmbPopubWindow.showAlert(this, Const.MOBILE_REGEX_ERROR);
            return;
        }
        XmbPopubWindow.showTranparentLoading(ReceiptAddressActivity.this);
        mPresenter.sendCode(UserService.gen_send_code_params(phone));
    }

    @OnClick(R.id.cancel)
    void cancelHideWheelView() {
        wheelView_container.setVisibility(View.GONE);
    }

    @OnClick(R.id.save)
    void save() {
        String receipt_user = receipt_address_user.getText().toString();
        String receipt_phone = receipt_address_phone.getText().toString();
        String receipt_address = receipt_address_address.getText().toString();
        String phone_code = receipt_phone_code.getText().toString();
        if (!CommonUtil.nEmptyStringValid(receipt_user)) {
            XmbPopubWindow.showAlert(this, "请输入收货人~");
            return;
        }
        if (!CommonUtil.nEmptyStringValid(receipt_phone)) {
            XmbPopubWindow.showAlert(this, "请输入手机号码~");
            return;
        }
        if (!CommonUtil.isMobilePhone(receipt_phone)) {
            XmbPopubWindow.showAlert(this, "手机号码格式不正确~");
            return;
        }
        if (!CommonUtil.validCode(phone_code)) {
            XmbPopubWindow.showAlert(this, "验证码输入错误~");
            return;
        }
        if (!CommonUtil.nEmptyStringValid(province_name)) {
            XmbPopubWindow.showAlert(this, "请选择省份~");
            return;
        }
        if (!CommonUtil.nEmptyStringValid(city_name)) {
            XmbPopubWindow.showAlert(this, "请选择市~");
            return;
        }
        if (!CommonUtil.nEmptyStringValid(district_name)) {
            XmbPopubWindow.showAlert(this, "请选择区县~");
            return;
        }
        if (!CommonUtil.nEmptyStringValid(receipt_address)) {
            XmbPopubWindow.showAlert(this, "请输入详细地址~");
            return;
        }
        XmbPopubWindow.showTranparentLoading(this);
        mPresenter.apply_shop_code(UserService.gen_apply_shop_code_params(receipt_user, receipt_phone, phone_code, province_name, city_name, district_name, receipt_address));
    }

    @OnClick(R.id.finish)
    void finishHideWheelView() {
        if (current_type == 1) {
            if (!provinces.get(wheelView.getSeletedIndex()).getCode().equals(province_id)) {
                province_name = provinces.get(wheelView.getSeletedIndex()).getName();
                provinceTextView.setText(provinces.get(wheelView.getSeletedIndex()).getName());
                cityTextView.setText("");
                districtTextView.setText("");
                province_id = provinces.get(wheelView.getSeletedIndex()).getCode();
                city_name = "";
                district_name = "";
            }
        } else if (current_type == 2) {
            if (!cities.get(wheelView.getSeletedIndex()).getCode().equals(city_id)) {
                city_name = cities.get(wheelView.getSeletedIndex()).getName();
                cityTextView.setText(cities.get(wheelView.getSeletedIndex()).getName());
                city_id = cities.get(wheelView.getSeletedIndex()).getCode();
                districtTextView.setText("");
                district_name = "";
            }
        } else if (current_type == 3) {
            district_name = districts.get(wheelView.getSeletedIndex()).getName();
            districtTextView.setText(districts.get(wheelView.getSeletedIndex()).getName());
        }
        wheelView_container.setVisibility(View.GONE);
    }

    protected void showWheelView(int type) {
        if (receipt_address_user.isFocused()) {
            InputSoftUtil.hideSoftInput(this, receipt_address_user);
        } else if (receipt_address_phone.isFocused()) {
            InputSoftUtil.hideSoftInput(this, receipt_address_phone);
        } else if (receipt_address_address.isFocused()) {
            InputSoftUtil.hideSoftInput(this, receipt_address_address);
        } else if (receipt_phone_code.isFocused()) {
            InputSoftUtil.hideSoftInput(this, receipt_phone_code);
        }
        current_type = type;
        wheelView_container.setVisibility(View.VISIBLE);
        setWheelView();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt_address);
        ButterKnife.bind(this);
        displayTitle();
        initApi();
    }

    protected void setWheelView() {
        wheelView.setOffset(2);
        if (current_type == 1) {
            provinces.clear();
            provinceList.clear();
            provinces = XmbDB.getInstance(this, AppContext.DBPath).loadProvinces();
            for (int i = 0; i < provinces.size(); i++) {
                provinceList.add(provinces.get(i).getName());
            }
            wheelView.setItems(provinceList);
        } else if (current_type == 2) {
            if (province_id.equals("")) {
                XmbPopubWindow.showAlert(this, "请选择省份");
                return;
            }
            cities.clear();
            cityList.clear();
            cities = XmbDB.getInstance(this, AppContext.DBPath).loadCities(province_id);
            for (int i = 0; i < cities.size(); i++) {
                cityList.add(cities.get(i).getName());
            }
            wheelView.setItems(cityList);
        } else if (current_type == 3) {
            if (city_id.equals("")) {
                XmbPopubWindow.showAlert(this, "请选择城市");
                return;
            }
            districts.clear();
            regionList.clear();
            districts = XmbDB.getInstance(this, AppContext.DBPath).loadCountries(city_id);
            districts.remove(0);
            for (int i = 0; i < districts.size(); i++) {
                regionList.add(districts.get(i).getName());
            }
            wheelView.setItems(regionList);
        }
        wheelView.setSeletion(0);

        wheelView.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
            @Override
            public void onSelected(int selectedIndex, String item) {

            }
        });
    }

    public void displayTitle() {
        toolBarTitleTextView.setText(toolbarTitle);
        receipt_address_user.setOnClickListener((view) -> cancelHideWheelView());
        receipt_address_phone.setOnClickListener((view) -> cancelHideWheelView());
        receipt_address_address.setOnClickListener((view) -> cancelHideWheelView());
        provinceTextView.setOnClickListener((view) -> showWheelView(1));
        cityTextView.setOnClickListener((view) -> showWheelView(2));
        districtTextView.setOnClickListener((view) -> showWheelView(3));
    }

    UserService mService;
    ReceiptAddressPresenter mPresenter;

    protected void initApi() {
        mService = new UserService();
        mPresenter = new ReceiptAddressPresenter(this, mService);
    }

    public void jumpToPay(ApplyShopCode.Order order) {
        Intent intent = new Intent(this, PayActivity.class);
        intent.putExtra("mobile", receipt_address_phone.getText().toString());
        intent.putExtra("order_amount_formatted", order.order_amount_formatted);
        intent.putExtra("order_amount", order.order_amount);
        intent.putExtra("order_sn", order.order_sn);
        intent.putExtra("ali_sign", order.ali_sign);
        startActivity(intent);
        finish();
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

}

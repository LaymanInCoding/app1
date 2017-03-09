package com.xiaomabao.weidian.views;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.bigkoo.pickerview.OptionsPickerView;
import com.bigkoo.pickerview.beans.ProvinceBean;
import com.xiaomabao.weidian.models.Region;
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
import java.util.Map;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BindCardActivity extends AppCompatActivity {

    @BindString(R.string.bind_card)
    String toolbarTitle;
    @BindView(R.id.toolbar_title)
    TextView toolbarTextView;
    @BindView(R.id.wheelView_container)
    View wheelView_container;
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

    private ArrayList<ProvinceBean> options1Items = new ArrayList<ProvinceBean>();
    private ArrayList<ArrayList<ProvinceBean>> options2Items = new ArrayList<ArrayList<ProvinceBean>>();
    private ArrayList<ArrayList<ArrayList<ProvinceBean>>> options3Items = new ArrayList<ArrayList<ArrayList<ProvinceBean>>>();
    OptionsPickerView pvOptions;
    OptionsPickerView pvOptions1;
    private String mProvinceId = "";
    private String mCityId = "";
    private String mDistrictId = "";
    private String mBankId = "";

    private int current_type = 0;
    private String deposit_bank = "";
    private String province_name = "";
    private String city_name = "";
    private String district_name = "";
    private String province_id = "", city_id = "";
    private static final String[] BANKS = new String[]{"中国银行", "中国工商银行", "中国建设银行", "中国农业银行", "招商银行", "交通银行"};
    private ArrayList<String> provinceList = new ArrayList<>();
    private ArrayList<String> cityList = new ArrayList<>();
    private ArrayList<String> regionList = new ArrayList<>();
    private List<Province> provinces = new ArrayList<>();
    private List<City> cities = new ArrayList<>();
    private List<Country> districts = new ArrayList<>();

    private void setPicker() {
        pvOptions = new OptionsPickerView(this);
        List<Region> regionListTest = XmbDB.getInstance(this, AppContext.DBPath).loadRegions();
        Map<String, List<Region>> map = new HashMap<>();
        for (Region region : regionListTest) {
            List<Region> regionTmp = new ArrayList<>();
            if (!map.containsKey(region.getParentId())) {
                regionTmp.add(region);
            } else {
                regionTmp = map.get(region.getParentId());
                regionTmp.add(region);
            }
            map.put(region.getParentId(), regionTmp);
        }

        for (Region region : map.get("1")) {
            options1Items.add(new ProvinceBean(region.getId(), region.getName()));
        }

        for (ProvinceBean province : options1Items) {
            List<Region> regionListTmp = map.get(province.getId());
            ArrayList<ProvinceBean> options2Items_tmp = new ArrayList<ProvinceBean>();
            for (Region region : regionListTmp) {
                options2Items_tmp.add(new ProvinceBean(region.getId(), region.getName()));
            }
            options2Items.add(options2Items_tmp);
        }

        for (int i = 0; i < options2Items.size(); i++) {
            ArrayList<ArrayList<ProvinceBean>> options3Items_0 = new ArrayList<ArrayList<ProvinceBean>>();
            ArrayList<ProvinceBean> tmp = options2Items.get(i);
            for (ProvinceBean province : tmp) {
                ArrayList<ProvinceBean> options3Items_0_1 = new ArrayList<ProvinceBean>();
                List<Region> regionListTmp = map.get(province.getId());
                if (regionListTmp != null) {
                    for (Region region : regionListTmp) {
                        options3Items_0_1.add(new ProvinceBean(region.getId(), region.getName()));
                    }
                }
                options3Items_0.add(options3Items_0_1);
            }
            options3Items.add(options3Items_0);
        }

        pvOptions.setPicker(options1Items, options2Items, options3Items, true);
        pvOptions.setTitle("选择地址");
        pvOptions.setCyclic(false, false, false);
        //设置默认选中的三级项目
        //监听确定选择按钮
        int pos1 = 0, pos2 = 0, pos3 = 0;
        if (!mProvinceId.equals("")) {
            pos1 = Integer.parseInt(mProvinceId);
        }

        if (!mCityId.equals("")) {
            pos2 = Integer.parseInt(mCityId);
        }

        if (!mDistrictId.equals("")) {
            pos3 = Integer.parseInt(mDistrictId);
        }

        pvOptions.setSelectOptions(pos1, pos2, pos3);
        pvOptions.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {

            @Override
            public void onOptionsSelect(int options1, int options2, int options3) {
                province_name = options1Items.get(options1).getPickerViewText();
                city_name = options2Items.get(options1).get(options2).getPickerViewText();
                district_name = options3Items.get(options1).get(options2).get(options3).getPickerViewText();
                provinceTextView.setText(province_name);
                cityTextView.setText(city_name);
                districtTextView.setText(district_name);
                mProvinceId = options1Items.get(options1).getId();
                mCityId = options2Items.get(options1).get(options2).getId();
                mDistrictId = options3Items.get(options1).get(options2).get(options3).getId();
            }
        });
    }

    private void setPicker1() {
        pvOptions1 = new OptionsPickerView(this);
        ArrayList<String> bankName = new ArrayList<>();
        for (int i = 0; i < BANKS.length; i++) {
            bankName.add(BANKS[i]);
        }
        pvOptions1.setPicker(bankName);
        pvOptions1.setTitle("银行名称");
        pvOptions1.setCyclic(false);
        //设置默认选中的三级项目
        //监听确定选择按钮
        int pos1 = 0;
        pvOptions1.setSelectOptions(pos1);
        pvOptions1.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {

            @Override
            public void onOptionsSelect(int options1, int options2, int options3) {
                deposit_bank = bankName.get(options1);
                depositBankTextView.setText(deposit_bank);

            }
        });
    }

    @OnClick(R.id.back)
    void back() {
        finish();
    }

    @OnClick(R.id.cancel)
    void cancelHideWheelView() {
        wheelView_container.setVisibility(View.GONE);
    }

    @OnClick(R.id.bind_card)
    void bind_card() {
        String account_name = accountEditText.getText().toString();
        String branch_bank = branchEditText.getText().toString();
        String card_no = cardNoEditText.getText().toString();
        String mobile_phone = phoneEditText.getText().toString();
        if (!CommonUtil.nEmptyStringValid(account_name)) {
            XmbPopubWindow.showAlert(this, "请输入您的姓名~");
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
        if (!CommonUtil.nEmptyStringValid(deposit_bank)) {
            XmbPopubWindow.showAlert(this, "请选择开户行~");
            return;
        }
        if (!CommonUtil.nEmptyStringValid(branch_bank)) {
            XmbPopubWindow.showAlert(this, "请输入开户行支行~");
            return;
        }
        if (!CommonUtil.nEmptyStringValid(card_no)) {
            XmbPopubWindow.showAlert(this, "请输入银行卡卡号~");
            return;
        }
        if (!CommonUtil.nEmptyStringValid(mobile_phone)) {
            XmbPopubWindow.showAlert(this, "请输入您银行卡绑定的手机号码~");
            return;
        }
        if (!CommonUtil.isMobilePhone(mobile_phone)) {
            XmbPopubWindow.showAlert(this, "手机号码格式错误~");
            return;
        }
        XmbPopubWindow.showTranparentLoading(this);
        HashMap<String, String> hashMap = ProfitService.gen_bind_card_params(AppContext.getToken(this), account_name, province_name, city_name, district_name, deposit_bank, branch_bank, card_no, mobile_phone);
        mPresenter.bind_card(hashMap);
    }

    @OnClick(R.id.finish)
    void finishHideWheelView() {
        if (current_type == 0) {
            deposit_bank = BANKS[wheelView.getSeletedIndex()];
            depositBankTextView.setText(BANKS[wheelView.getSeletedIndex()]);
        } else if (current_type == 1) {
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
        current_type = type;
        setWheelView();
        wheelView_container.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_card);
        ButterKnife.bind(this);
        setPicker();
        setPicker1();
        setView();
        initApi();
    }

    protected void setView() {
        toolbarTextView.setText(toolbarTitle);
        accountEditText.setOnClickListener((view) -> cancelHideWheelView());
        branchEditText.setOnClickListener((view) -> cancelHideWheelView());
        phoneEditText.setOnClickListener((view) -> cancelHideWheelView());

        depositBankTextView.setOnClickListener((view) -> pvOptions1.show());
        provinceTextView.setOnClickListener((view) -> pvOptions.show());
        cityTextView.setOnClickListener((view) -> pvOptions.show());
        districtTextView.setOnClickListener((view) -> pvOptions.show());
    }

    protected void setWheelView() {
        wheelView.setOffset(2);

        if (current_type == 0) {
            wheelView.setItems(Arrays.asList(BANKS));
        } else {
            if (current_type == 1) {
                provinces.clear();
                provinceList.clear();
                provinces = XmbDB.getInstance(this, AppContext.DBPath).loadProvinces();
                for (int i = 0; i < provinces.size(); i++) {
                    provinceList.add(provinces.get(i).getName());
                }
                wheelView.setItems(provinceList);
            } else if (current_type == 2) {
                if (province_name.equals("")) {
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
        }
        wheelView.setSeletion(0);

        wheelView.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
            @Override
            public void onSelected(int selectedIndex, String item) {
                Log.d("selected", "selectedIndex: " + selectedIndex + ", item: " + item);
            }
        });
    }


    public void bindCardCallback() {
        setResult(RESULT_OK);
        finish();
    }

    ProfitService mService;
    BindCardPresenter mPresenter;

    protected void initApi() {
        mService = new ProfitService();
        mPresenter = new BindCardPresenter(this, mService);
    }
}

package yijiabaoforpad.yijiabao.aihuizhongyi.com.switchdemo;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {

    private SwitchView switchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        switchView=findViewById(R.id.switchView);

        switchView.setAnimationDuration(200);
        switchView.setSwitchIsOpen(false);

    }
}

package name.caiyao.wechatautojump;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private Process process;

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what){
                case 0:
                    Toast.makeText(MainActivity.this,"未获取到Root权限，程序无法运行！",Toast.LENGTH_LONG).show();
                    break;
                case 1:
                    Toast.makeText(MainActivity.this,"5秒钟后开始启动，请于5秒内打开微信跳一跳，并点击开始游戏",Toast.LENGTH_LONG).show();
                    break;
            }
            return true;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button start = findViewById(R.id.start);
        Button stop = findViewById(R.id.stop);
        new Thread(){
            @Override
            public void run() {
                Utils.testRoot();
            }
        }.start();
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            if (!Utils.testRoot()){
                                handler.sendEmptyMessage(0);
                                return;
                            }
                            handler.sendEmptyMessage(1);
                            Thread.sleep(5000);
                            Utils.copyFromAssetsToFile("jump", getApplicationContext());
                            Utils.suCmd(new String[]{"dd if=" + getFilesDir() + "/jump of=/data/local/tmp/jump" }, Runtime.getRuntime().exec("su"));
                            process = Runtime.getRuntime().exec("su");
                            Utils.suCmd(new String[]{"chmod +x /data/local/tmp/jump", "cd  /data/local/tmp/", "./jump 2.04" }, process);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        });
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread() {
                    @Override
                    public void run() {
                        Utils.suCmd(new String[]{"exit"}, process);
                    }
                }.start();
            }
        });

    }
}

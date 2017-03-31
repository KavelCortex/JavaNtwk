package cn.kavel.demo.javantwk.fragments;

import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import cn.kavel.demo.javantwk.R;
import cn.kavel.demo.javantwk.core.SpamCheck;
import cn.kavel.demo.javantwk.interfaces.Experiment;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by wjw_w on 2017/3/11.
 */
public class Exp1Fragment extends Fragment implements Experiment {

    private View rootView;

    //@Override
    public String getExperimentTitle() {
        return getString(R.string.exp1_title);
    }

    //@Override
    public String getExperimentDescription() {
        return getString(R.string.exp1_desc1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_exp1, container, false);
        initWrapper();
        initHint();
        initButton();
        return rootView;
    }

    private void initWrapper() {
        TextView wrapper = (TextView) rootView.findViewById(R.id.weakNetworkWrapper);
        wrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new asCheckNetworkState().execute();
            }
        });
        wrapper.performClick();
    }

    private void initHint() {
        TextView tv = (TextView) rootView.findViewById(R.id.hint);

        SpannableString spannableString = new SpannableString(getString(R.string.exp1_desc2));
        spannableString.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View view) {
                Log.v("Spannable", "clicked");
                new AlertDialog.Builder(getActivity()).setTitle(R.string.exp1_desc2)
                        .setMessage(R.string.exp1_desc3)
                        .create().show();
            }
        }, 0, spannableString.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);

        tv.setText(spannableString);
        tv.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void initButton() {
        Button btn = (Button) rootView.findViewById(R.id.btn_go);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText et = (EditText) rootView.findViewById(R.id.exp1_input);
                TextView tv = (TextView) rootView.findViewById(R.id.result);
                tv.setText("正在检测，请保持网络连通性...");
                String address = et.getText().toString();
                new asGetSpamResult().execute(address);
            }
        });
    }

    class asCheckNetworkState extends AsyncTask<String, Boolean, Boolean> {

        final EditText et = (EditText) rootView.findViewById(R.id.exp1_input);
        final Button btn = (Button) rootView.findViewById(R.id.btn_go);
        final TextView wrapper = (TextView) rootView.findViewById(R.id.weakNetworkWrapper);
        int progressCount = 0;
        int successCount = 0;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            et.setEnabled(false);
            btn.setEnabled(false);
            wrapper.setVisibility(View.VISIBLE);
            wrapper.setText(getString(R.string.exp1_wrapper2));
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            String[] testUrls = {"www.kavel.cn", "www.baidu.com", "www.szu.edu.cn"};
            for (String testUrl : testUrls) {
                try {
                    InetAddress.getByName(testUrl);
                    publishProgress(true);
                } catch (UnknownHostException e) {
                    publishProgress(false);
                }
            }
            return true;
        }

        @Override
        protected void onProgressUpdate(Boolean... trials) {
            super.onProgressUpdate(trials);
            for (Boolean isSuccess : trials) {
                progressCount++;
                if (isSuccess)
                    successCount++;
                wrapper.setText("正在测试连通率：\n成功\\尝试：" + successCount + "\\" + progressCount);
                Log.v("ChkNS", "正在测试连通率：\n成功\\尝试：" + successCount + "\\" + progressCount);
            }
        }

        @Override
        protected void onPostExecute(Boolean s) {
            super.onPostExecute(s);
            boolean isSuccess = successCount != 0;
            et.setEnabled(isSuccess);
            btn.setEnabled(isSuccess);
            wrapper.setVisibility(isSuccess ? View.GONE : View.VISIBLE);
            wrapper.setText(getText(isSuccess ? R.string.exp1_wrapper1 : R.string.exp1_wrapper3));
            Log.v("ChkNS", isSuccess ? "Success" : "Fail");
        }
    }

    class asGetSpamResult extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... addresses) {
            StringBuilder result = new StringBuilder("检测结果：");
            for (String address : addresses) {
                try {
                    result.append(SpamCheck.getResult(address));
                    result.append("\n");
                } catch (IllegalArgumentException e) {
                    result.append("输入的地址为空，请重新输入。");
                }
            }
            return result.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            TextView tv = (TextView) rootView.findViewById(R.id.result);
            tv.setText(result);
        }
    }

}

package cn.kavel.demo.javantwk.fragments;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.*;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import cn.kavel.demo.javantwk.R;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import java.io.*;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Exp4Fragment extends Fragment {

    public static final String URL_HOMEPAGE = "http://www.szu.edu.cn/szu.asp";
    private View mRootView;
    private WebView mWebView;
    private ProgressDialog mProgressDialog;
    private URL mCurrentPageURL;
    private int mCurrentPort;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_exp4, container, false);
        initView();
        initWebView();
        return mRootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    private void initView() {
        mRootView.findViewById(R.id.iv_btn_forward).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String urls = ((EditText) mRootView.findViewById(R.id.et_address)).getText().toString();
                if (!urls.contains("://"))
                    mWebView.loadUrl("http://" + urls);
                else
                    mWebView.loadUrl(urls);
            }
        });
        mRootView.findViewById(R.id.iv_btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mWebView.canGoBack()) {
                    mWebView.goBack();
                }
            }
        });
        mRootView.findViewById(R.id.iv_btn_front).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mWebView.canGoForward()) {
                    mWebView.goForward();
                }
            }
        });
        mRootView.findViewById(R.id.iv_btn_refresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mWebView.reload();
            }
        });
        ((EditText) mRootView.findViewById(R.id.et_address)).setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionID, KeyEvent keyEvent) {
                if (actionID == EditorInfo.IME_ACTION_GO) {
                    mRootView.findViewById(R.id.iv_btn_forward).performClick();
                }
                return true;
            }
        });
        mRootView.findViewById(R.id.et_address).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean isFocused) {
                TextView tv = (TextView) view;
                Drawable icSearch = getResources().getDrawable(R.drawable.ic_search_black_18dp);
                Drawable icFile = getResources().getDrawable(R.drawable.ic_insert_drive_file_black_18dp);
                icSearch.setBounds(0, 0, icSearch.getMinimumWidth(), icSearch.getMinimumHeight());
                icFile.setBounds(0, 0, icFile.getMinimumWidth(), icFile.getMinimumHeight());

                tv.setCompoundDrawables(isFocused ? icSearch : icFile, null, null, null);

            }
        });
        mRootView.findViewById(R.id.iv_btn_download).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCurrentPort == 443)
                    new AlertDialog.Builder(getActivity()).setTitle(R.string.exp4_address_not_supported_title)
                            .setMessage(R.string.exp4_address_not_supported_msg).create().show();
                else if (mCurrentPort == 80) {
                    mProgressDialog = new ProgressDialog(getActivity());
                    mProgressDialog = new ProgressDialog(getActivity());
                    mProgressDialog.setTitle(R.string.exp2_dig_stage_1_title);
                    mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    mProgressDialog.setMessage(getString(R.string.exp2_dig_stage_1_msg));
                    mProgressDialog.setIndeterminate(false);
                    mProgressDialog.show();
                    new asGetContent().execute();
                }
            }
        });
    }

    private void initWebView() {
        getActivity().getWindow().setFormat(PixelFormat.TRANSLUCENT);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        mWebView = (WebView) mRootView.findViewById(R.id.webview);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView webView, String url) {
                super.onPageFinished(webView, url);
                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(mWebView.getTitle());
                ((EditText) mRootView.findViewById(R.id.et_address)).setText(url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap bitmap) {
                super.onPageStarted(view, url, bitmap);
                try {
                    mCurrentPageURL = new URL(view.getUrl());
                    if (url.startsWith("http://"))
                        mCurrentPort = 80;
                    else if (url.startsWith("https://"))
                        mCurrentPort = 443;
                    else
                        mCurrentPort = -1;
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

                mRootView.findViewById(R.id.iv_btn_back).setEnabled(view.canGoBack());
                mRootView.findViewById(R.id.iv_btn_front).setEnabled(view.canGoForward());
                mRootView.findViewById(R.id.webview).requestFocus();
            }
        });

        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        ((EditText) mRootView.findViewById(R.id.et_address)).setText(URL_HOMEPAGE);
        mWebView.loadUrl(URL_HOMEPAGE);
    }

    class asGetContent extends AsyncTask<String, String, String> {


        // 获取src标签正则
        private static final String IMGURL_REG = "src=\"(.*?)\"";

        @Override
        protected String doInBackground(String... strings) {
            String saveLocation = Environment.getExternalStorageDirectory() + "/Download/" + mCurrentPageURL.getHost();
            String HTML = getHTML(mCurrentPageURL.toString(), saveLocation, 1, 1);
            List<String> listSrcTag = getSrcTag(HTML);
            List<String> listSrcURL = getSrcURL(listSrcTag);
            int numberCurrent = -1;
            int numberQuery = -1;
            numberQuery = listSrcURL.size();
            for (String srcURL : listSrcURL) {
                numberCurrent = listSrcURL.indexOf(srcURL) + 1;
                Log.d("DownloadQuery", numberCurrent + "of" + numberQuery);
                downloadSrc(srcURL, saveLocation, numberCurrent, numberQuery);
                Log.d("DownloadQuery", numberCurrent + "completed");
            }
            return saveLocation;
        }

        @Override
        protected void onPostExecute(final String s) {
            super.onPostExecute(s);
            mProgressDialog.dismiss();
            new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.exp2_dig_stage_3_title)
                    .setMessage(getString(R.string.exp2_dig_stage_3_msg) + " " + s)
                    .setPositiveButton(getString(R.string.exp2_dig_stage_3_btn_done), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).create().show();
        }

        @Override
        protected void onProgressUpdate(String... values) {
            Log.d("onProgressUpdate", values[2] + "of" + values[3]);
            super.onProgressUpdate(values);
            String fileName = values[0];
            String saveLocation = values[1];
            int numberCurrent = Integer.parseInt(values[2]);
            int numberQuery = Integer.parseInt(values[3]);
            mProgressDialog.setMax(numberQuery);
            mProgressDialog.setProgress(numberCurrent);
            mProgressDialog.setTitle(getString(R.string.exp2_dig_stage_2_title));
            mProgressDialog.setMessage(getString(R.string.exp4_download_file_msg1) + " " + fileName +
                    getString(R.string.exp4_download_file_msg2) +
                    "\r\n" + saveLocation +
                    "\r\n" + getString(R.string.exp4_download_take_times_msg));
        }

        private void downloadSrc(String src, String saveLocation, int numberCurrent, int numberQuery) {
            try {
                URL urlSrc = new URL(src);
                URLConnection connection = urlSrc.openConnection();
                File dir = new File(saveLocation);
                if (!dir.exists())
                    dir.mkdir();
                String fileName = src.substring(src.lastIndexOf("/") + 1, src.length());
                String filepath = dir.getAbsolutePath() + "/" + fileName;
                File savingFile = new File(filepath);

                publishProgress(fileName, saveLocation, numberCurrent + "", numberQuery + "");

                Log.d("DownloadSrc", "src:" + src +
                        "\r\nto:" + savingFile.getAbsolutePath());
                int len = -1;
                byte[] buffer = new byte[4096];
                InputStream in = connection.getInputStream();
                OutputStream out = new FileOutputStream(savingFile);
                while ((len = in.read(buffer)) != -1) {
                    out.write(buffer, 0, len);
                }
                out.flush();
                out.close();
                in.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private String getHTML(String src, String saveLocation, int numberCurrent, int numberQuery) {
            try {
                URL srcURL = new URL(src);
                Socket socket = new Socket(srcURL.getHost(), 80);
                BufferedWriter writerToSocket = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
                StringBuilder httpRequestHeader = new StringBuilder();
                httpRequestHeader.append("GET " + srcURL.toString() + " HTTP/1.1\r\n");
                httpRequestHeader.append("User-Agent: " + mWebView.getSettings().getUserAgentString() + " JavaNtwk/0.0.4\r\n");
                httpRequestHeader.append("HOST: " + srcURL.getHost() + "\r\n");
                httpRequestHeader.append("Connection: close\r\n");
                httpRequestHeader.append("Accept-Charset: UTF-8\r\n");
                httpRequestHeader.append("Accept-Language: zh-CN,en;q=0.5\r\n");
                httpRequestHeader.append("Accept: text/html,application/xhtml+xml;q=0.9,*/*;q=0.8\r\n");
                httpRequestHeader.append("\r\n");
                Log.d("Request", httpRequestHeader.toString());
                writerToSocket.write(httpRequestHeader.toString());
                writerToSocket.flush();

                File dir = new File(saveLocation);
                if (!dir.exists())
                    dir.mkdir();
                String fileName = "index.html";
                String filepath = dir.getAbsolutePath() + "/" + fileName;
                File savingFile = new File(filepath);

                publishProgress(fileName, saveLocation, numberCurrent + "", numberQuery + "");

                Log.d("Download", "src:" + src +
                        "\r\nto:" + savingFile.getAbsolutePath());
                BufferedReader readerFromSocket = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
                BufferedWriter writerToFile = new BufferedWriter(new FileWriter(savingFile));
                StringBuilder sb = new StringBuilder();
                String line;
                boolean canWrite = false;
                while ((line = readerFromSocket.readLine()) != null) {
                    if (!canWrite) {
                        if (line.isEmpty()) {
                            canWrite = true;
                        } else {
                            Log.d("Response", line);
                        }
                    } else {
                        Log.d("File", line);
                        sb.append(line);
                        writerToFile.write(line);
                        writerToFile.newLine();
                        writerToFile.flush();
                    }
                }
                writerToFile.close();
                readerFromSocket.close();

                writerToSocket.close();
                return sb.toString();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return "";
        }

        private List<String> getSrcTag(String HTML) {
            Matcher matcher = Pattern.compile(IMGURL_REG).matcher(HTML);
            List<String> listImgUrl = new ArrayList<>();
            while (matcher.find()) {
                listImgUrl.add(matcher.group());
            }
            return listImgUrl;
        }

        private List<String> getSrcURL(List<String> listSrcUrl) {
            List<String> listImgSrc = new ArrayList<>();
            for (String src : listSrcUrl) {
                String path = src.split("\"")[1];
                if (path.startsWith("//"))
                    path = "http:" + path;
                if (!path.startsWith("http://"))
                    path = "http://" + mCurrentPageURL.getHost() + "/" + path;
                if (path.contains("?"))
                    path = path.substring(0, path.indexOf("?"));
                listImgSrc.add(path);
            }
            return listImgSrc;
        }


    }
}

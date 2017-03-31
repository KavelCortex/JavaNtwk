package cn.kavel.demo.javantwk.fragments;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.*;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import cn.kavel.demo.javantwk.R;
import cn.kavel.demo.javantwk.interfaces.Experiment;
import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.UUID;


public class Exp2Fragment extends Fragment implements Experiment {

    public static final int REQUEST_CODE_OPEN_DIRECTORY = 1;
    public static final String COMMAND_RESOLVE_AND_DOWNLOAD = "Resolve and Download";
    public static final String COMMAND_RESOLVE_ONLY = "Resolve Only";

    private View mRootView;
    private URL mTargetURL;
    private URLConnection mTargetURLConnection;
    private int mContentLength;
    private ContentInfo mTargetFileInfo;
    private String mDownloadPath;
    private File mFileSaved;

    private ProgressDialog mProgressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_exp2, container, false);
        init();
        return mRootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    private void init() {
        final EditText etInput = (EditText) mRootView.findViewById(R.id.et_input);
        Button btnPaste = (Button) mRootView.findViewById(R.id.btn_paste);
        btnPaste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etInput.setText(getClipContent());
            }
        });

        Button btnDir = (Button) mRootView.findViewById(R.id.btn_dir);
        btnDir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                startActivityForResult(intent, REQUEST_CODE_OPEN_DIRECTORY);
            }
        });

        Button btnGo = (Button) mRootView.findViewById(R.id.btn_go);
        btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String queryString = etInput.getText().toString();
                mProgressDialog = new ProgressDialog(getActivity());
                mProgressDialog.setTitle(R.string.exp2_dig_stage_1_title);
                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                mProgressDialog.setMessage(getString(R.string.exp2_dig_stage_1_msg));
                mProgressDialog.setIndeterminate(true);
                mProgressDialog.show();
                new asResolveURL().execute(queryString, COMMAND_RESOLVE_AND_DOWNLOAD);
            }
        });

        Button btnResolveOnly = (Button) mRootView.findViewById(R.id.btn_resolve_only);
        btnResolveOnly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String queryString = etInput.getText().toString();
                new asResolveURL().execute(queryString, COMMAND_RESOLVE_ONLY);
            }
        });

        mDownloadPath = Environment.getExternalStorageDirectory() + "/Download";
        EditText etPath = (EditText) mRootView.findViewById(R.id.et_path);
        etPath.setText(mDownloadPath);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_OPEN_DIRECTORY && resultCode == Activity.RESULT_OK) {
            Uri treeUri = data.getData();
            String docID = DocumentsContract.getTreeDocumentId(treeUri);
            mDownloadPath = Environment.getExternalStorageDirectory() + "/" + docID.split(":")[1];
            EditText etPath = (EditText) mRootView.findViewById(R.id.et_path);
            etPath.setText(mDownloadPath);
            Log.v("getDirectory", mDownloadPath);
        }
    }

    private String getClipContent() {
        ClipboardManager clipboardManager = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        if (!clipboardManager.hasPrimaryClip())
            return "";
        ClipData clipData = clipboardManager.getPrimaryClip();
        return clipData.getItemAt(0).getText().toString();
    }

    class asResolveURL extends AsyncTask<String, String, String> {

        boolean isSuccess;
        String mode;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... args) {
            String query = args[0];
            mode = args[1];
            StringBuilder sb = new StringBuilder(getString(R.string.exp2_prefix_result));

            try {
                isSuccess = false;
                if (query.isEmpty())
                    throw new IllegalArgumentException();
                mTargetURL = new URL(query);
                mTargetURLConnection = mTargetURL.openConnection();
                mTargetURLConnection.connect();
                mContentLength = mTargetURLConnection.getContentLength();
                BufferedInputStream bis = new BufferedInputStream(mTargetURLConnection.getInputStream());
                ContentInfoUtil util = new ContentInfoUtil();
                mTargetFileInfo = util.findMatch(bis);
                sb.append(mTargetFileInfo.getMessage());
                Log.v("fileType", mTargetFileInfo.getMessage());
                isSuccess = true;
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                sb.append(getString(R.string.exp2_exception_empty));
            } catch (IOException e) {
                e.printStackTrace();
                sb.append(getString(R.string.exp2_exception_unable_to_resolve));
            }

            return sb.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            TextView tvResultResolve = (TextView) mRootView.findViewById(R.id.tv_result_resolve);
            tvResultResolve.setText(result);
            if (mode.equals(COMMAND_RESOLVE_AND_DOWNLOAD)) {
                mProgressDialog.setMessage(result);
                if (isSuccess)
                    new asDownloadFile().execute();
                else
                    mProgressDialog.dismiss();
            }
        }
    }

    class asDownloadFile extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog.setTitle(R.string.exp2_dig_stage_2_title);
            mProgressDialog.setMessage(
                    getString(R.string.exp2_prefix_result) + mTargetFileInfo.getMessage() + "\n" +
                            getString(R.string.exp2_dig_stage_2_msg) + " " + mDownloadPath);
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setMax(mContentLength);
        }


        @Override
        protected String doInBackground(String... strings) {
            try {
                mTargetURLConnection = mTargetURL.openConnection();
                File dir = new File(mDownloadPath);
                if (!dir.exists())
                    dir.mkdir();
                String filename = mDownloadPath + "/" + UUID.randomUUID() + "." + mTargetFileInfo.getName();
                mFileSaved = new File(filename);
                InputStream in = mTargetURLConnection.getInputStream();
                int progress = 0;
                int len = -1;
                byte[] buffer = new byte[4096];
                OutputStream out = new FileOutputStream(mFileSaved);
                while ((len = in.read(buffer)) != -1) {
                    out.write(buffer, 0, len);
                    progress += len;
                    onProgressUpdate(progress);
                }
                out.flush();
                out.close();
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            for (int value : values) {
                mProgressDialog.setProgress(value);
            }

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            mProgressDialog.dismiss();
            new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.exp2_dig_stage_3_title)
                    .setMessage(getString(R.string.exp2_dig_stage_3_msg) + " " + mFileSaved.getAbsolutePath())
                    .setPositiveButton(getString(R.string.exp2_dig_stage_3_btn_open), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent();
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.setAction(Intent.ACTION_VIEW);
                            intent.setDataAndType(Uri.fromFile(mFileSaved), mTargetFileInfo.getMimeType());
                            startActivity(intent);
                        }
                    })
                    .setNeutralButton(getString(R.string.exp2_dig_stage_3_btn_done), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).create().show();
        }
    }

}

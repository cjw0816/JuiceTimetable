package com.juice.timetable.ui.login;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.juice.timetable.R;
import com.juice.timetable.data.http.EduInfo;
import com.juice.timetable.data.http.LeaveInfo;
import com.juice.timetable.databinding.FragmentLoginBinding;
import com.juice.timetable.utils.LogUtils;

/**
 * 修改认证页面相应功能实现类
 */
public class LoginFragment extends Fragment {
    private FragmentLoginBinding binding;
    private LoginViewModel loginViewModel;
    private DrawerLayout drawer;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
//        View root = inflater.inflate(R.layout.fragment_login, container, false);
//        return root;
        binding = FragmentLoginBinding.inflate(getLayoutInflater());//binding初始化
//        binding = DataBindingUtil.setContentView(requireActivity(), R.layout.fragment_login);
        drawer = requireActivity().findViewById(R.id.drawer_layout);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnDialogClick();
        allNull();
        btnPromptNull();
        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                if (drawerView == null) return;
                if (isAdded()) {
                    InputMethodManager inputMethodManager = (InputMethodManager) requireContext().getApplicationContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(drawerView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
    }

    /**
     * 点击用户协议，弹出对话框
     */
    private void btnDialogClick() {
        binding.btnUserItem.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onDialogClick(binding.btnUserItem);
            }
        });

    }


    /**
     * 在什么都没输入时，提示学号未输入
     */
    private void allNull() {
        String sno = binding.etSno.getText().toString().trim();
        String edu = binding.etEduPassword.getText().toString().trim();
        String leave = binding.etLeavePassword.getText().toString().trim();
        if (sno.isEmpty() && edu.isEmpty() && leave.isEmpty()) {
            snoNull();
        }
    }


    /**
     * 监听文本内容，对学号，教务网密码，请假系统密码相应的判断
     */
    private void btnPromptNull() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                judgmentIsEmpty();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        binding.etSno.addTextChangedListener(textWatcher);//监听里面的内容
        binding.etEduPassword.addTextChangedListener(textWatcher);//监听里面的内容
        binding.etLeavePassword.addTextChangedListener(textWatcher);//监听里面的内容
    }

    /**
     * 判断是否为空，并提示相应信息
     */
    private void judgmentIsEmpty() {
        final String sno = binding.etSno.getText().toString().trim();
        final String edu = binding.etEduPassword.getText().toString().trim();
        final String leave = binding.etLeavePassword.getText().toString().trim();
        if (sno.isEmpty() && edu.isEmpty() && leave.isEmpty()) {
            snoNull();
        }
        if (sno.isEmpty() && edu.isEmpty() && !leave.isEmpty()) {
            snoNull();
        }
        if (sno.isEmpty() && !edu.isEmpty() && leave.isEmpty()) {
            snoNull();
        }
        if (sno.isEmpty() && !edu.isEmpty() && !leave.isEmpty()) {
            snoNull();
        }
        if (!sno.isEmpty() && edu.isEmpty() && leave.isEmpty()) {
            eduPasswordNull();
        }
        if (!sno.isEmpty() && edu.isEmpty() && !leave.isEmpty()) {
            eduPasswordNull();
        }
        if (!sno.isEmpty() && !edu.isEmpty() && leave.isEmpty()) {
            binding.btnGo.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    preTwoJudge();
                }
            });
        }
        if (!sno.isEmpty() && !edu.isEmpty() && !leave.isEmpty()) {
            binding.btnGo.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
//                    preAllJudge();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            // 教务网验证
                            LogUtils.getInstance().d("教务网前端验证成功");
                            String uri = "http://jwb.fdzcxy.com/kb/zkb_xs.asp";
                            try {
                                EduInfo.getTimeTable(sno, edu, uri, getContext().getApplicationContext());
                            } catch (Exception e) {
                                String errorText = e.getMessage();
                                LogUtils.getInstance().d("errorText:" + errorText);
                                //解决在子线程中调用Toast的异常情况处理
                                Looper.prepare();

                                Toast.makeText(getContext().getApplicationContext(), errorText, Toast.LENGTH_SHORT).show();

                                //解决在子线程中调用Toast的异常情况处理-结束需要添加这句
                                Looper.loop();
                            }
                            LogUtils.getInstance().d("教务网密码验证成功");
                            // 跳转到课表首页

                            // 添加了下面三行，后面的请假系统验证将不执行
                            if (leave == "") {
                                Looper.prepare();
                                Toast.makeText(getContext().getApplicationContext(), "教务网密码验证成功", Toast.LENGTH_SHORT).show();
                                Looper.loop();
                                // 只填写了教务网密码 跳转课表首页
                                // 
                            }


                            // 请假系统验证
                            LogUtils.getInstance().d("请假系统前端验证成功");
                            uri = "http://mis.fdzcxy.com/index.php?n=stuwork-dormcheck-record-student&c=dormcheckrecordstudent";
                            try {
                                LeaveInfo.getLeave(sno, leave, uri, getContext().getApplicationContext());
                            } catch (Exception e) {
                                String errorText = e.getMessage();
                                LogUtils.getInstance().d("errorText:" + errorText);
                                //解决在子线程中调用Toast的异常情况处理
                                Looper.prepare();
                                Toast.makeText(getContext().getApplicationContext(), errorText, Toast.LENGTH_SHORT).show();
                                //解决在子线程中调用Toast的异常情况处理-结束需要添加这句
                                Looper.loop();
                            }
                            LogUtils.getInstance().d("教务网和请假系统密码均验证成功");
                            // 跳转到课表首页

                            Looper.prepare();
                            Toast.makeText(getContext().getApplicationContext(), "教务网和请假系统密码均验证成功", Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }
                    }).start();
                }
            });
        }

    }

    /**
     * 点击按钮，提示未输入学号
     */
    private void snoNull() {
        binding.btnGo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(requireActivity(), "未输入学号", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 点击按钮，提示未输入教务网密码
     */
    private void eduPasswordNull() {
        binding.btnGo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(requireActivity(), "未输入教务网密码", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 点击按钮，提示未输入请假系统密码
     */
    private void leavePasswordNull() {
        hideSoftKeyboard(requireActivity());
        Toast.makeText(requireActivity(), "进入主界面，仅显示课表，签到提示功能、本班未签功能停用", Toast.LENGTH_SHORT).show();
    }

    private void preAllJudge() {
        String snoStr = binding.etSno.getText().toString().trim();
        //对学号状态的预判断
        if (snoStr.length() != 9) {
            Toast.makeText(requireActivity(), "请输入九位数字的学号", Toast.LENGTH_SHORT).show();
        } else {
            judgmentContent();
        }
    }

    private void preTwoJudge() {
        String snoStr = binding.etSno.getText().toString().trim();
        String eduStr = binding.etEduPassword.getText().toString().trim();
        //对学号状态的预判断
        if (snoStr.length() != 9) {
            if (eduStr.length() < 6) {
                Toast.makeText(requireActivity(), "学号密码不符合规范", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireActivity(), "请输入九位数字的学号", Toast.LENGTH_SHORT).show();
            }
        } else {
            if (eduStr.length() < 6) {
                Toast.makeText(requireActivity(), "请输入至少六位数的密码", Toast.LENGTH_SHORT).show();
            } else {
                judgmentSnoEdu();
            }
        }
    }


    /**
     * 学号不为空，教务网密码不为空，请假系统密码为空时，对学号和教务网密码的判断
     */
    private void judgmentSnoEdu() {
        String sno = binding.etSno.getText().toString();
        String edu = binding.etEduPassword.getText().toString().trim();
        if (!sno.matches(transferSno()) && !edu.equals(transferEduPassword("123456"))) {
            snoError();
        }
        if (!sno.matches(transferSno()) && edu.equals(transferEduPassword("123456"))) {
            snoError();
        }
        if (sno.matches(transferSno()) && !edu.equals(transferEduPassword("123456"))) {
            eduPasswordError();
        }
        if (sno.matches(transferSno()) && edu.equals(transferEduPassword("123456"))) {
            leavePasswordNull();
        }
    }

    /**
     * 判断学号，教务网密码，请假系统密码，并弹出提示框相应的内容
     */
    private void judgmentContent() {
        String sno = binding.etSno.getText().toString();
        String edu = binding.etEduPassword.getText().toString().trim();
        String leave = binding.etLeavePassword.getText().toString().trim();

        if (!sno.matches(transferSno()) && !edu.equals(transferEduPassword("123456"))
                && !leave.equals(transferLeavePassword("123456"))) {
            snoError();
        }
        if (!sno.matches(transferSno()) && !edu.equals(transferEduPassword("123456"))
                && leave.equals(transferLeavePassword("123456"))) {
            snoError();
        }
        if (!sno.matches(transferSno()) && edu.equals(transferEduPassword("123456"))
                && !leave.equals(transferLeavePassword("123456"))) {
            snoError();
        }
        if (sno.matches(transferSno()) && !edu.equals(transferEduPassword("123456"))
                && !leave.equals(transferLeavePassword("123456"))) {
            snoError();
        }
        if (sno.matches(transferSno()) && !edu.equals(transferEduPassword("123456"))
                && !leave.equals(transferLeavePassword("123456"))) {
            eduPasswordError();
        }
        if (sno.matches(transferSno()) && !edu.equals(transferEduPassword("123456"))
                && leave.equals(transferLeavePassword("123456"))) {
            eduPasswordError();
        }
        if (sno.matches(transferSno()) && edu.equals(transferEduPassword("123456"))
                && !leave.equals(transferLeavePassword("123456"))) {
            leavePasswordError();
        }
        if (sno.matches(transferSno()) && edu.equals(transferEduPassword("123456"))
                && leave.equals(transferLeavePassword("123456"))) {
            hideSoftKeyboard(requireActivity());
            Toast.makeText(requireActivity(), "验证成功", Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * 提示框，提示“学号错误”
     */
    private void snoError() {
        Toast.makeText(requireActivity(), "学号错误", Toast.LENGTH_SHORT).show();
    }

    /**
     * 提示框，提示“教务网密码错误”
     */
    private void eduPasswordError() {
        Toast.makeText(requireActivity(), "教务网密码错误", Toast.LENGTH_SHORT).show();
    }

    /**
     * 提示框，提示“请假系统密码错误”
     */
    private void leavePasswordError() {
        Toast.makeText(requireActivity(), "请假系统密码错误", Toast.LENGTH_SHORT).show();
    }


    private String transferSno() {
        return "21\\d{7}";
    }

    private String transferEduPassword(String str) {
        return str;
    }

    private String transferLeavePassword(String str) {
        return str;
    }

    /**
     * 对话框的一个实现
     *
     * @param
     */
    private void onDialogClick(View v) {
        new AlertDialog.Builder(getContext())
                .setTitle("用户条款")
                .setMessage("本应用深知个人信息对您的重要性，并会尽全力保护您的个人信息安全可靠。我们致力于维持您对我们的信任，恪守以下原则，保护您的个人信息：权责一致原则、目的明确原则、选择同意原则、最少够用原则、确保安全原则、主体参与原则、公开透明原则等。同时，我们承诺，我们将按业界成熟的安全标准，采取相应的安全保护措施来保护您的个人信息。 请在使用我们的产品（或服务）前，仔细阅读并了解本《隐私权政策》。\n" +
                        "一、我们如何收集和使用您的个人信息\n" +
                        "个人信息是指以电子或者其他方式记录的能够单独或者与其他信息结合识别特定自然人身份或者反映特定自然人活动情况的各种信息。 我们仅会出于本政策所述的以下目的，收集和使用您的个人信息：\n" +
                        "（一）我们不出售任何商品，同时也不展示任何商品\n" +
                        "（二）开展内部数据分析和研究，第三方SDK统计服务，改善我们的产品或服务\n" +
                        "我们收集数据是根据您与我们的互动和您所做出的选择，包括您的隐私设置以及您使用的产品和功能。我们收集的数据可能包括SDK/API/JS代码版本、浏览器、互联网服务提供商、IP地址、平台、时间戳、应用标识符、应用程序版本、应用分发渠道、独立设备标识符、iOS广告标识符（IDFA)、安卓广告主标识符、网卡（MAC）地址、国际移动设备识别码（IMEI）、设备型号、终端制造厂商、终端设备操作系统版本、会话启动/停止时间、语言所在地、时区和网络状态（WiFi等）、硬盘、CPU和电池使用情况等。\n" +
                        "当我们要将信息用于本策略未载明的其它用途时，会事先征求您的同意。\n" +
                        "当我们要将基于特定目的收集而来的信息用于其他目的时，会事先征求您的同意。\n" +
                        "二、我们如何使用 Cookie 和同类技术\n" +
                        "（一）Cookie\n" +
                        "为确保软件正常运转，我们会在您的计算机或移动设备上存储名为 Cookie 的小数据文件。Cookie 通常包含标识符、站点名称以及一些号码和字符。借助于 Cookie，软件能够存储您的个人账户信息等数据。\n" +
                        "我们不会将 Cookie 用于本政策所述目的之外的任何用途。您可根据自己的偏好管理或删除 Cookie。您可以清除设备上保存的所有 Cookie，大部分网络浏览器都设有阻止 Cookie 的功能。但如果您这么做，则需要在每一次访问我们的软件时亲自更改用户设置。\n" +
                        "（二）Do Not Track（请勿追踪）\n" +
                        "很多网络浏览器均设有 Do Not Track 功能，该功能可向软件发布 Do Not Track 请求。目前，主要互联网标准组织尚未设立相关政策来规定软件应如何应对此类请求。但如果您的浏览器启用了 Do Not Track，那么我们的所有软件都会尊重您的选择。\n" +
                        "三、我们如何共享、转让、公开披露您的个人信息\n" +
                        "（一）共享\n" +
                        "我们不会向其他任何公司、组织和个人分享您的个人信息，但以下情况除外：\n" +
                        "1、在获取明确同意的情况下共享：获得您的明确同意后，我们会与其他方共享您的个人信息。\n" +
                        "2、我们可能会根据法律法规规定，或按政府主管部门的强制性要求，对外共享您的个人信息。\n" +
                        "3、与我们的关联公司共享：您的个人信息可能会与我们关联公司共享。我们只会共享必要的个人信息，且受本隐私政策中所声明目的的约束。关联公司如要改变个人信息的处理目的，将再次征求您的授权同意。\n" +
                        "我们的关联公司包括:【无】。\n" +
                        "4、与授权合作伙伴共享：仅为实现本隐私权政策中声明的目的，我们的某些服务将由授权合作伙伴提供。我们可能会与合作伙伴共享您的某些个人信息，以提供更好的客户服务和用户体验。例如，我们聘请来提供第三方数据统计和分析服务的公司可能需要采集和访问个人数据以进行数据统计和分析。在这种情况下，这些公司 必须遵守我们的数据隐私和安全要求。我们仅会出于合法、正当、必要、特定、明确的目的共享您的个人信息，并且只会共享提供服务所必要的个人信息。\n" +
                        "（二）转让\n" +
                        "我们不会将您的个人信息转让给任何公司、组织和个人，但以下情况除外：\n" +
                        "1、在获取明确同意的情况下转让：获得您的明确同意后，我们会向其他方转让您的个人信息；\n" +
                        "2、在涉及合并、收购或破产清算时，如涉及到个人信息转让，我们会在要求新的持有您个人信息的公司、组织继续受此隐私政策的约束，否则我们将要求该公司、组织重新向您征求授权同意。\n" +
                        "（三）公开披露\n" +
                        "我们仅会在以下情况下，公开披露您的个人信息：\n" +
                        "1、获得您明确同意后；\n" +
                        "2、基于法律的披露：在法律、法律程序、诉讼或政府主管部门强制性要求的情况下，我们可能会公开披露您的个人信息。\n" +
                        "四、我们如何保护您的个人信息\n" +
                        "（一）我们已使用符合业界标准的安全防护措施保护您提供的个人信息，防止数据遭到未经授权访问、公开披露、使用、修改、损坏或丢失。我们会采取一切合理可行的措施，保护您的个人信息。例如，在您的浏览器与“服务”之间交换数据（如信用卡信息）时受 SSL 加密保护；我们同时对我们软件提供 https 安全浏览方式；我们会使用加密技术确保数据的保密性；我们会使用受信赖的保护机制防止数据遭到恶意攻击；我们会部署访问控制机制，确保只有授权人员才可访问个人信息；以及我们会举办安全和隐私保护培训课程，加强员工对于保护个人信息重要性的认识。\n" +
                        "（二）我们会采取一切合理可行的措施，确保未收集无关的个人信息。我们只会在达成本政策所述目的所需的期限内保留您的个人信息，除非需要延长保留期或受到法律的允许。\n" +
                        "（三）互联网并非绝对安全的环境，而且电子邮件、即时通讯、及与其他我们用户的交流方式并未加密，我们强烈建议您不要通过此类方式发送个人信息。请使用复杂密码，协助我们保证您的账号安全。\n" +
                        "（四）互联网环境并非百分之百安全，我们将尽力确保或担保您发送给我们的任何信息的安全性。如果我们的物理、技术、或管理防护设施遭到破坏，导致信息被非授权访问、公开披露、篡改、或毁坏，导致您的合法权益受损，我们将承担相应的法律责任。\n" +
                        "（五）在不幸发生个人信息安全事件后，我们将按照法律法规的要求，及时向您告知：安全事件的基本情况和可能的影响、我们已采取或将要采取的处置措施、您可自主防范和降低风险的建议、对您的补救措施等。我们将及时将事件相关情况以邮件、信函、电话、推送通知等方式告知您，难以逐一告知个人信息主体时，我们会采取合理、有效的方式发布公告。\n" +
                        "同时，我们还将按照监管部门要求，主动上报个人信息安全事件的处置情况。\n" +
                        "五、您的权利\n" +
                        "按照中国相关的法律、法规、标准，以及其他国家、地区的通行做法，我们保障您对自己的个人信息行使以下权利：\n" +
                        "（一）访问您的个人信息\n" +
                        "您有权访问您的个人信息，法律法规规定的例外情况除外。如果您想行使数据访问权，可以通过以下方式自行访问：\n" +
                        "账户信息——如果您希望访问或编辑您的账户中的个人资料信息和支付信息、更改您的密码、添加安全信息或关闭您的账户等，您可以通过访问执行此类操作。\n" +
                        "搜索信息——您可以在应用中访问或清除您的搜索历史记录、查看和修改兴趣以及管理其他数据。\n" +
                        "六、本隐私权政策如何更新\n" +
                        "我们可能适时会对本隐私权政策进行调整或变更，本隐私权政策的任何更新将以标注更新时间的方式公布在我们软件上，除法律法规或监管规定另有强制性规定外，经调整或变更的内容一经通知或公布后的7日后生效。如您在隐私权政策调整或变更后继续使用我们提供的任一服务或访问我们相关软件的，我们相信这代表您已充分阅读、理解并接受修改后的隐私权政策并受其约束。\n")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                }).create().show();

    }


    /**
     * 强制隐藏软键盘
     *
     * @param activity
     */
    private static void hideSoftKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0); //强制隐藏键盘
    }


}

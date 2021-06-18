package HeavenTao.Media;

import android.app.Activity;
import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.PowerManager;
import android.os.SystemClock;
import android.os.Process;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import HeavenTao.Audio.*;
import HeavenTao.Video.*;
import HeavenTao.Data.*;

//媒体处理线程类。
public abstract class MediaProcThread extends Thread
{
    public String m_CurClsNameStrPt = this.getClass().getSimpleName(); //存放当前类名称digit符串。

    public int m_RunFlag = RUN_FLAG_NORUN; //存放this 线程运行标记。
    public static final int RUN_FLAG_NORUN = 0; //运行标记：未开始运行。
    public static final int RUN_FLAG_INIT = 1; //运行标记：刚开始运行正 в 初始化。
    public static final int RUN_FLAG_PROC = 2; //运行标记：初始化完毕正 в 循环处理 frame 。
    public static final int RUN_FLAG_DESTROY = 3; //运行标记：跳出循环处理 frame 正 в destroy。
    public static final int RUN_FLAG_END = 4; //运行标记：destroy完毕。
    public int m_ExitFlag = 0; //存放this 线程退出标记， for 0表示保持运行， for 1表示请求退出， for 2表示请求重启， for 3表示请求重启但不执行 user 定义的UserInit初始化函 number 和UserDestroydestroy函 number 。
    public int m_ExitCode = 0; //存放this 线程退出代码， for 0表示正常退出， for -1表示初始化失败， for -2表示处理失败。

    public static Context m_AppContextPt; //存放应用程序上下文类对象的内存指针。

    int m_IsSaveSettingToFile = 0; //存放是否 Save Настраивать到file， for 非0表示要 Save ， for 0表示不 Save 。
    String m_SettingFileFullPathStrPt; //存放Настраиватьfile的完整路径digit符串。

    public int m_IsPrintLogcat = 0; //存放是否 print LogcatLog， for 非0表示要 print ， for 0表示不 print 。

    int m_IsUseWakeLock; //存放是否 Use wake lock ，非0表示要 use ，0表示 Do not use 。
    PowerManager.WakeLock m_ProximityScreenOffWakeLockPt; //存放接近息屏唤醒锁类对象的内存指针。
    PowerManager.WakeLock m_FullWakeLockPt; //存放屏幕键盘全亮唤醒锁类对象的内存指针。

    public class AudioInput // Audio  enter 类。
    {
        public int m_IsUseAudioInput; //存放是否 use  Audio  enter ， for 0表示 Do not use ， for 非0表示要 use 。

        public int m_SamplingRate = 16000; //存放 Sampling frequency ，取值只能 for 8000、16000、32000。
        public int m_FrameLen = 320; //存放 frame 的 number According to the length ， unit 采样 number 据，取值只能 for 10 millisecond的 Times number 。例如：8000Hz的10 millisecond for 80、20 millisecond for 160、30 millisecond for 240，16000Hz的10 millisecond for 160、20 millisecond for 320、30 millisecond for 480，32000Hz的10 millisecond for 320、20 millisecond for 640、30 millisecond for 960。

        public int m_IsUseSystemAecNsAgc = 0; //存放是否 Use the acoustic feedback that comes with the system sound Canceller, noise sound Suppressor and automatic gain controller （ The system does not necessarily come with it ）， for 0表示 Do not use ， for 非0表示要 use 。

        public int m_UseWhatAec = 0; //存放 use 什么 Acoustic echo sound Eliminator ， for 0表示 Do not use ， for 1表示Speex Acoustic echo sound Eliminator ， for 2表示WebRtc Fixed-point version  Acoustic echo sound Eliminator ， for 2表示WebRtc Floating point version  Acoustic echo sound Eliminator ， for 4表示SpeexWebRtc triple  Acoustic echo sound Eliminator 。

        SpeexAec m_SpeexAecPt; //存放Speex Acoustic echo sound Eliminator 类对象的内存指针。
        int m_SpeexAecFilterLen; //存放Speex Acoustic echo sound Eliminator 的滤波器 number According to the length ， unit  millisecond。
        int m_SpeexAecIsUseRec; //存放Speex Acoustic echo sound Eliminator 是否 use  Устранение остаточного звука ， for 非0表示要 use ， for 0表示 Do not use 。
        float m_SpeexAecEchoMultiple; //存放Speex Acoustic echo sound Eliminator  в  Устранение остаточного звука  Time ， Remnants back to the sound  Times number ， Times number  The bigger the elimination, the stronger ， The value range is [0.0,100.0]。
        float m_SpeexAecEchoCont; //存放Speex Acoustic echo sound Eliminator  в  Устранение остаточного звука  Time ， Remnants back to the sound 持续系 number ，系 number  The bigger the elimination, the stronger ， The value range is [0.0,0.9]。
        int m_SpeexAecEchoSupes; //存放Speex Acoustic echo sound Eliminator  в  Устранение остаточного звука  Time ， The decibel value of the maximum attenuation of the residual return sound ，The smaller the decibel value, the greater the attenuation ， The value range is [-2147483648,0]。
        int m_SpeexAecEchoSupesAct; //存放Speex Acoustic echo sound Eliminator  в  Устранение остаточного звука  Time ， Have近端  voice sound live动 Time  The decibel value of the maximum attenuation of the residual return sound ，The smaller the decibel value, the greater the attenuation ， The value range is [-2147483648,0]。
        int m_SpeexAecIsSaveMemFile; //存放Speex Acoustic echo sound Eliminator 是否 Save 内存块到file， for 非0表示要 Save ， for 0表示不 Save 。
        String m_SpeexAecMemFileFullPathStrPt; //存放Speex Acoustic echo sound Eliminator Memory block file完整路径digit符串类对象的内存指针。

        WebRtcAecm m_WebRtcAecmPt; //存放WebRtc Fixed-point version  Acoustic echo sound Eliminator 类对象的内存指针。
        int m_WebRtcAecmIsUseCNGMode; //存放WebRtc Fixed-point version  Acoustic echo sound Eliminator 是否 use  Comfortable noise  sound generation mode， for 非0表示要 use ， for 0表示 Do not use 。
        int m_WebRtcAecmEchoMode; //存放WebRtc Fixed-point version  Acoustic echo sound Eliminator 的Elimination mode，Elimination modeMorehigh Eliminate More Strong ， The value range is [0,4]。
        int m_WebRtcAecmDelay; //存放WebRtc Fixed-point version  Acoustic echo sound Eliminator 的回sound延迟， unit  millisecond， The value range is [-2147483648,2147483647]， for 0 Represents adaptive Настраивать。

        WebRtcAec m_WebRtcAecPt; //存放WebRtc Floating point version  Acoustic echo sound Eliminator 类对象的内存指针。
        int m_WebRtcAecEchoMode; //存放WebRtc Floating point version  Acoustic echo sound Eliminator 的Elimination mode，Elimination modeMorehigh Eliminate More Strong ， The value range is [0,2]。
        int m_WebRtcAecDelay; //存放WebRtc Floating point version  Acoustic echo sound Eliminator 的回sound延迟， unit  millisecond， The value range is [-2147483648,2147483647]， for 0 Represents adaptive Настраивать。
        int m_WebRtcAecIsUseDelayAgnosticMode; //存放WebRtc Floating point version  Acoustic echo sound Eliminator 是否 use 回sound延迟不可知模式， for 非0表示要 use ， for 0表示 Do not use 。
        int m_WebRtcAecIsUseExtdFilterMode; //存放WebRtc Floating point version  Acoustic echo sound Eliminator 是否 use 扩展滤波器模式， for 非0表示要 use ， for 0表示 Do not use 。
        int m_WebRtcAecIsUseRefinedFilterAdaptAecMode; //存放WebRtc Floating point version  Acoustic echo sound Eliminator 是否 use  Refined filter adaptive Aec模式， for 非0表示要 use ， for 0表示 Do not use 。
        int m_WebRtcAecIsUseAdaptAdjDelay; //存放WebRtc Floating point version  Acoustic echo sound Eliminator 是否 use  Adaptive adjustment  Return to sound delay ， for 非0表示要 use ， for 0表示 Do not use 。
        int m_WebRtcAecIsSaveMemFile; //存放WebRtc Floating point version  Acoustic echo sound Eliminator 是否 Save 内存块到file， for 非0表示要 Save ， for 0表示不 Save 。
        String m_WebRtcAecMemFileFullPathStrPt; //存放WebRtc Floating point version  Acoustic echo sound Eliminator Memory block file完整路径digit符串。

        SpeexWebRtcAec m_SpeexWebRtcAecPt; //存放SpeexWebRtc triple  Acoustic echo sound Eliminator 类对象的内存指针。
        int m_SpeexWebRtcAecWorkMode; //存放SpeexWebRtc triple  Acoustic echo sound Eliminator 的工作模式， for 1表示Speex Acoustic echo sound Eliminator +WebRtc Fixed-point version  Acoustic echo sound Eliminator ， for 2表示WebRtc Fixed-point version  Acoustic echo sound Eliminator +WebRtc Floating point version  Acoustic echo sound Eliminator ， for 3表示Speex Acoustic echo sound Eliminator +WebRtc Fixed-point version  Acoustic echo sound Eliminator +WebRtc Floating point version  Acoustic echo sound Eliminator 。
        int m_SpeexWebRtcAecSpeexAecFilterLen; //存放SpeexWebRtc triple  Acoustic echo sound Eliminator 的Speex Acoustic echo sound Eliminator 的滤波器 number According to the length ， unit  millisecond。
        int m_SpeexWebRtcAecSpeexAecIsUseRec; //存放SpeexWebRtc triple  Acoustic echo sound Eliminator 的Speex Acoustic echo sound Eliminator 是否 use  Устранение остаточного звука ， for 非0表示要 use ， for 0表示 Do not use 。
        float m_SpeexWebRtcAecSpeexAecEchoMultiple; //存放SpeexWebRtc triple  Acoustic echo sound Eliminator 的Speex Acoustic echo sound Eliminator  в  Устранение остаточного звука  Time ， Remnants back to the sound  Times number ， Times number  The bigger the elimination, the stronger ， The value range is [0.0,100.0]。
        float m_SpeexWebRtcAecSpeexAecEchoCont; //存放SpeexWebRtc triple  Acoustic echo sound Eliminator 的Speex Acoustic echo sound Eliminator  в  Устранение остаточного звука  Time ， Remnants back to the sound 持续系 number ，系 number  The bigger the elimination, the stronger ， The value range is [0.0,0.9]。
        int m_SpeexWebRtcAecSpeexAecEchoSupes; //存放SpeexWebRtc triple  Acoustic echo sound Eliminator 的Speex Acoustic echo sound Eliminator  в  Устранение остаточного звука  Time ， The decibel value of the maximum attenuation of the residual return sound ，The smaller the decibel value, the greater the attenuation ， The value range is [-2147483648,0]。
        int m_SpeexWebRtcAecSpeexAecEchoSupesAct; //存放SpeexWebRtc triple  Acoustic echo sound Eliminator 的Speex Acoustic echo sound Eliminator  в  Устранение остаточного звука  Time ， Have近端  voice sound live动 Time  The decibel value of the maximum attenuation of the residual return sound ，The smaller the decibel value, the greater the attenuation ， The value range is [-2147483648,0]。
        int m_SpeexWebRtcAecWebRtcAecmIsUseCNGMode; //存放SpeexWebRtc triple  Acoustic echo sound Eliminator 的WebRtc Fixed-point version  Acoustic echo sound Eliminator 是否 use  Comfortable noise  sound generation mode， for 非0表示要 use ， for 0表示 Do not use 。
        int m_SpeexWebRtcAecWebRtcAecmEchoMode; //存放SpeexWebRtc triple  Acoustic echo sound Eliminator 的WebRtc Fixed-point version  Acoustic echo sound Eliminator 的Elimination mode，Elimination modeMorehigh Eliminate More Strong ， The value range is [0,4]。
        int m_SpeexWebRtcAecWebRtcAecmDelay; //存放SpeexWebRtc triple  Acoustic echo sound Eliminator 的WebRtc Fixed-point version  Acoustic echo sound Eliminator 的回sound延迟， unit  millisecond， The value range is [-2147483648,2147483647]， for 0 Represents adaptive Настраивать。
        int m_SpeexWebRtcAecWebRtcAecEchoMode; //存放SpeexWebRtc triple  Acoustic echo sound Eliminator 的WebRtc Floating point version  Acoustic echo sound Eliminator 的Elimination mode，Elimination modeMorehigh Eliminate More Strong ， The value range is [0,2]。
        int m_SpeexWebRtcAecWebRtcAecDelay; //存放SpeexWebRtc triple  Acoustic echo sound Eliminator 的WebRtc Floating point version  Acoustic echo sound Eliminator 的回sound延迟， unit  millisecond， The value range is [-2147483648,2147483647]， for 0 Represents adaptive Настраивать。
        int m_SpeexWebRtcAecWebRtcAecIsUseDelayAgnosticMode; //存放SpeexWebRtc triple  Acoustic echo sound Eliminator 的WebRtc Floating point version  Acoustic echo sound Eliminator 是否 use 回sound延迟不可知模式， for 非0表示要 use ， for 0表示 Do not use 。
        int m_SpeexWebRtcAecWebRtcAecIsUseExtdFilterMode; //存放SpeexWebRtc triple  Acoustic echo sound Eliminator 的WebRtc Floating point version  Acoustic echo sound Eliminator 是否 use 扩展滤波器模式， for 非0表示要 use ， for 0表示 Do not use 。
        int m_SpeexWebRtcAecWebRtcAecIsUseRefinedFilterAdaptAecMode; //存放SpeexWebRtc triple  Acoustic echo sound Eliminator 的WebRtc Floating point version  Acoustic echo sound Eliminator 是否 use  Refined filter adaptive Aec模式， for 非0表示要 use ， for 0表示 Do not use 。
        int m_SpeexWebRtcAecWebRtcAecIsUseAdaptAdjDelay; //存放SpeexWebRtc triple  Acoustic echo sound Eliminator 的WebRtc Floating point version  Acoustic echo sound Eliminator 是否 use  Adaptive adjustment  Return to sound delay ， for 非0表示要 use ， for 0表示 Do not use 。
        int m_SpeexWebRtcAecIsUseSameRoomAec; //存放SpeexWebRtc triple  Acoustic echo sound Eliminator 是否 use 同一房间 Acoustic echo sound消除， for 非0表示要 use ， for 0表示 Do not use 。
        int m_SpeexWebRtcAecSameRoomEchoMinDelay; //存放SpeexWebRtc triple  Acoustic echo sound Eliminator 的同一房间回sound最小延迟， unit  millisecond， The value range is [1,2147483647]。

        public int m_UseWhatNs = 0; //存放 use 什么 noise sound Suppressor ， for 0表示 Do not use ， for 1表示Speex Preprocessor  noise sound торможение ， for 2表示WebRtc Fixed-point version  noise sound Suppressor ， for 3表示WebRtc Floating point version  noise sound Suppressor ， for 4表示RNNoise noise sound Suppressor 。

        SpeexPproc m_SpeexPprocPt; //存放Speex预处理器类对象的内存指针。
        int m_SpeexPprocIsUseNs; //存放Speex预处理器是否 use  noise sound торможение ， for 非0表示要 use ， for 0表示 Do not use 。
        int m_SpeexPprocNoiseSupes; //存放Speex预处理器 в  noise sound торможение  Time ， noise sound  Decibel value of maximum attenuation ，The smaller the decibel value, the greater the attenuation ， The value range is [-2147483648,0]。
        int m_SpeexPprocIsUseDereverb; //存放Speex预处理器是否 use   Reverberation sound cancellation ， for 非0表示要 use ， for 0表示 Do not use 。

        WebRtcNsx m_WebRtcNsxPt; //存放WebRtc Fixed-point version  noise sound Suppressor 类对象的内存指针。
        int m_WebRtcNsxPolicyMode; //存放WebRtc Fixed-point version  noise sound Suppressor 的 Strategy mode， Strategy modeMorehigh торможение More强， The value range is [0,3]。

        WebRtcNs m_WebRtcNsPt; //存放WebRtc Floating point version  noise sound Suppressor 类对象的内存指针。
        int m_WebRtcNsPolicyMode; //存放WebRtc Floating point version  noise sound Suppressor 的 Strategy mode， Strategy modeMorehigh торможение More强， The value range is [0,3]。

        RNNoise m_RNNoisePt; //存放RNNoise noise sound Suppressor 类对象的内存指针。

        int m_IsUseSpeexPprocOther; //存放Speex预处理器是否 use  Other functions ， for 非0表示要 use ， for 0表示 Do not use 。
        int m_SpeexPprocIsUseVad; //存放Speex预处理器是否 use   voice   sound activity detection ， for 非0表示要 use ， for 0表示 Do not use 。
        int m_SpeexPprocVadProbStart; //存放Speex预处理器 в   voice   sound activity detection  Time ，  Never   voice sound  Active  voice sound  Activity judgement percentage ratio rate ，概 rate More  Big is More difficult to judge for  Have  voice sound live， The value range is [0,100]。
        int m_SpeexPprocVadProbCont; //存放Speex预处理器 в   voice   sound activity detection  Time ，从 Have  voice sound  live to nothing  voice sound  Activity judgement percentage ratio rate ，概 rate More大More容易判断 for 无  voice sound live动， The value range is [0,100]。
        int m_SpeexPprocIsUseAgc; //存放Speex预处理器是否 use 自动增益控制， for 非0表示要 use ， for 0表示 Do not use 。
        int m_SpeexPprocAgcLevel; //存放Speex预处理器 в 自动增益控制 Time ，增益的目标等级，目标等级More大增益More大， The value range is [1,2147483647]。
        int m_SpeexPprocAgcIncrement; //存放Speex预处理器 в 自动增益控制 Time ，每秒最大增益的分贝值，分贝值More大增益More大， The value range is [0,2147483647]。
        int m_SpeexPprocAgcDecrement; //存放Speex预处理器 в 自动增益控制 Time ，每秒最大减益的分贝值，分贝值More小减益More大， The value range is [-2147483648,0]。
        int m_SpeexPprocAgcMaxGain; //存放Speex预处理器 в 自动增益控制 Time ，最大增益的分贝值，分贝值More大增益More大， The value range is [0,2147483647]。

        public int m_UseWhatEncoder = 0; //存放 use 什么 Encoder ， for 0表示PCM Raw data ， for 1表示Speex Encoder ， for 2表示Opus Encoder 。

        SpeexEncoder m_SpeexEncoderPt; //存放Speex Encoder 类对象的内存指针。
        int m_SpeexEncoderUseCbrOrVbr; //存放Speex Encoder  use  fixed ratiospecial rate 还是 dynamic ratiospecial rate 进行编码， for 0表示要 use  fixed ratiospecial rate ， for 非0表示要 use  dynamic ratiospecial rate 。
        int m_SpeexEncoderQuality; //存放Speex Encoding quality level of the encoder ， The higher the quality level, the better the quality 、 compression  rate More low ， The value range is [0,10]。
        int m_SpeexEncoderComplexity; //存放Speex Encoder encoding  the complexity ， the complexity  The higher the compression rate is unchanged 、CPU use  rate Morehigh、 The better the sound quality ， The value range is [0,10]。
        int m_SpeexEncoderPlcExpectedLossRate; //存放Speex Encoder  в  number  Packet loss concealment Time ， number According to the estimated packet loss rate ， Estimated loss rate  The higher the anti-network jitter, the stronger、 compression  rate More low ， The value range is [0,100]。

        public int m_IsSaveAudioToFile = 0; //存放是否 Save  Audio 到file， for 非0表示要 Save ， for 0表示不 Save 。
        WaveFileWriter m_AudioInputWaveFileWriterPt; //存放 Audio  enter Wavefile写入器对象的内存指针。
        WaveFileWriter m_AudioResultWaveFileWriterPt; //存放 Audio  result Wavefile写入器对象的内存指针。
        String m_AudioInputFileFullPathStrPt; //存放 Audio  enter file的完整路径digit符串。
        String m_AudioResultFileFullPathStrPt; //存放 Audio  result file的完整路径digit符串。

        AudioRecord m_AudioInputDevicePt; //存放 Audio input device类对象的内存指针。
        int m_AudioInputDeviceBufSz; //存放 Audio input device缓冲区大小， unit digit节。
        int m_AudioInputDeviceIsMute = 0; //存放 Audio input device是否 Quiet sound， for 0表示 Have声sound， for 非0表示 Quiet sound。

        public LinkedList< short[] > m_AudioInputFrameLnkLstPt; //存放 Audio  enter  frame Linked list类对象的内存指针。
        public LinkedList< short[] > m_AudioInputIdleFrameLnkLstPt; //存放 Audio  enter 空闲 frame Linked list类对象的内存指针。

        AudioInputThread m_AudioInputThreadPt; //存放 Audio  enter 线程类对象的内存指针。
    }

    public AudioInput m_AudioInputPt = new AudioInput(); //存放 Audio  enter 类对象的内存指针。

    public class AudioOutput // Audio  Output 类。
    {
        public int m_IsUseAudioOutput; //存放是否 use  Audio  Output ， for 0表示 Do not use ， for 非0表示要 use 。

        public int m_SamplingRate = 16000; //存放 Sampling frequency ，取值只能 for 8000、16000、32000。
        public int m_FrameLen = 320; //存放 frame 的 number According to the length ， unit 采样 number 据，取值只能 for 10 millisecond的 Times number 。例如：8000Hz的10 millisecond for 80、20 millisecond for 160、30 millisecond for 240，16000Hz的10 millisecond for 160、20 millisecond for 320、30 millisecond for 480，32000Hz的10 millisecond for 320、20 millisecond for 640、30 millisecond for 960。

        public int m_UseWhatDecoder = 0; //存放 use 什么解码器， for 0表示PCM Raw data ， for 1表示Speex解码器， for 2表示Opus解码器。

        SpeexDecoder m_SpeexDecoderPt; //存放Speex解码器类对象的内存指针。
        int m_SpeexDecoderIsUsePerceptualEnhancement; //存放Speex解码器是否 use 知觉增强， for 非0表示要 use ， for 0表示 Do not use 。

        public int m_IsSaveAudioToFile = 0; //存放是否 Save  Audio 到file， for 非0表示要 Save ， for 0表示不 Save 。
        WaveFileWriter m_AudioOutputWaveFileWriterPt; //存放 Audio  Output Wavefile写入器对象的内存指针。
        String m_AudioOutputFileFullPathStrPt; //存放 Audio  Output file的完整路径digit符串。

        public AudioTrack m_AudioOutputDevicePt; //存放 Audio  Output device 类对象的内存指针。
        int m_AudioOutputDeviceBufSz; //存放 Audio  Output device 缓冲区大小， unit digit节。
        public int m_UseWhatAudioOutputDevice = 0; //存放 use 什么 Audio  Output device ， for 0表示speaker， for 非0表示earpiece。
        public int m_UseWhatAudioOutputStreamType = 0; //存放 use 什么 Audio  Output 流 Types of ， for 0表示通话 Types of ， for 非0表示媒体 Types of 。
        int m_AudioOutputDeviceIsMute = 0; //存放 Audio  Output device 是否 Quiet sound， for 0表示 Have声sound， for 非0表示 Quiet sound。

        public LinkedList< short[] > m_AudioOutputFrameLnkLstPt; //存放 Audio  Output  frame Linked list类对象的内存指针。
        public LinkedList< short[] > m_AudioOutputIdleFrameLnkLstPt; //存放 Audio  Output 空闲 frame Linked list类对象的内存指针。

        AudioOutputThread m_AudioOutputThreadPt; //存放 Audio  Output 线程类对象的内存指针。
    }

    public AudioOutput m_AudioOutputPt = new AudioOutput(); //存放 Audio  Output 类对象的内存指针。

    public class VideoInput //video enter 类。
    {
        public int m_IsUseVideoInput; //存放是否 use video enter ， for 0表示 Do not use ， for 非0表示要 use 。

        public int m_MaxSamplingRate = 24; //存放最大 Sampling frequency ，取值范围 for [1,60]，实际 frame  rate 和图像的亮度 Have关，亮度较high Time  Sampling frequency 可以达到最大值，亮度较 low  Time 系统就自动降 low  Sampling frequency 来提升亮度。
        public int m_FrameWidth = 640; //存放 frame 的宽度， unit  for 像素。
        public int m_FrameHeight = 480; //存放 frame 的high度， unit  for 像素。

        public int m_UseWhatEncoder = 0; //存放 use 什么 Encoder ， for 0表示YU12 Raw data ， for 1表示OpenH264 Encoder 。

        OpenH264Encoder m_OpenH264EncoderPt; //存放OpenH264 Encoder 类对象的内存指针。
        int m_OpenH264EncoderVideoType;//存放OpenH264 Encoder 的video Types of ， for 0 Represents a real-time cameravideo， for 1 Represents real-time screen content video， for 2 Indicates a non-real-time camera video， for 3 Represents non-real-time screen content video， for 4 Means other video。
        int m_OpenH264EncoderEncodedBitrate; //存放OpenH264 Encoder 的 Bit rate after encoding ， unit  for bps。
        int m_OpenH264EncoderBitrateControlMode; //存放OpenH264 Encoder 的 Bit rate control mode ， for 0 Represents quality priority mode ， for 1 Indicates bit rate priority mode ， for 2 Indicates buffer priority mode ， for 3 Indicates the time stamp priority mode 。
        int m_OpenH264EncoderIDRFrameIntvl; //存放OpenH264 Encoder 的IDR Frame interval ， unit  for 个， for 0 Means only the first frame  for IDR frame ， for  more than the 0 Means every so  frame  At least one IDR frame 。
        int m_OpenH264EncoderComplexity; //存放OpenH264 Encoder 的 the complexity ， the complexity  The higher the compression rate is unchanged 、CPU TThe higher the usage rate, the better the picture quality ， The value range is [0,2]。

        public Camera m_VideoInputDevicePt; //存放videoinput device类对象的内存指针。
        public int m_UseWhatVideoInputDevice = 0; //存放 use 什么videoinput device， for 0表示rear camera， for 1表示Front camera。
        public HTSurfaceView m_VideoInputPreviewSurfaceViewPt; //存放video enter 预览SurfaceView类对象的内存指针。
        public byte m_VideoInputPreviewCallbackBufferPtPt[][]; //存放video enter 预览回调函 number 缓冲区的内存指针。
        public int m_VideoInputFrameRotate; //存放video enter  frame 旋转的角度，只能 for 0、90、180、270。
        int m_VideoInputDeviceIsBlack = 0; //存放videoinput device是否 Black screen， for 0表示 Have图像， for 非0表示 Black screen。

        public LinkedList< byte[] > m_NV21VideoInputFrameLnkLstPt; //存放NV21格式video enter  frame Linked list类对象的内存指针。
        public LinkedList< VideoInputFrameElm > m_VideoInputFrameLnkLstPt; //存放video enter  frame Linked list类对象的内存指针。
        public LinkedList< VideoInputFrameElm > m_VideoInputIdleFrameLnkLstPt; //存放video enter 空闲 frame Linked list类对象的内存指针。

        VideoInputThread m_VideoInputThreadPt; //存放video enter 线程类对象的内存指针。
    }
    public class VideoInputFrameElm //video enter  frame Linked list元素类。
    {
        VideoInputFrameElm()
        {
            m_RotateYU12VideoInputFramePt = ( m_VideoInputPt.m_IsUseVideoInput != 0 ) ? new byte[ m_VideoInputPt.m_FrameWidth * m_VideoInputPt.m_FrameHeight * 3 / 2 ] : null;
            m_RotateYU12VideoInputFrameWidthPt = ( m_VideoInputPt.m_IsUseVideoInput != 0 ) ? new HTInt() : null;
            m_RotateYU12VideoInputFrameHeightPt = ( m_VideoInputPt.m_IsUseVideoInput != 0 ) ? new HTInt() : null;
            m_EncoderVideoInputFramePt = ( m_VideoInputPt.m_IsUseVideoInput != 0 && m_VideoInputPt.m_UseWhatEncoder != 0 ) ? new byte[ m_VideoInputPt.m_FrameWidth * m_VideoInputPt.m_FrameHeight * 3 / 2 ] : null;
            m_EncoderVideoInputFrameLenPt = ( m_VideoInputPt.m_IsUseVideoInput != 0 && m_VideoInputPt.m_UseWhatEncoder != 0 ) ? new HTLong( 0 ) : null;
        }
        byte m_RotateYU12VideoInputFramePt[]; //存放旋转后YU12格式video enter  frame 的内存指针。
        HTInt m_RotateYU12VideoInputFrameWidthPt; //存放旋转后YU12格式video enter  frame 的宽度。
        HTInt m_RotateYU12VideoInputFrameHeightPt; //存放旋转后YU12格式video enter  frame 的high度。
        byte m_EncoderVideoInputFramePt[]; //存放已编码格式video enter  frame 。
        HTLong m_EncoderVideoInputFrameLenPt; //存放已编码格式video enter  frame 的 number According to the length ， unit digit节。
    }

    public VideoInput m_VideoInputPt = new VideoInput(); //存放video enter 类对象的内存指针。

    public class VideoOutput //video Output 类。
    {
        public int m_IsUseVideoOutput; //存放是否 use video Output ， for 0表示 Do not use ， for 非0表示要 use 。

        public int m_FrameWidth = 640; //存放 frame 的宽度， unit  for 像素。
        public int m_FrameHeight = 480; //存放 frame 的high度， unit  for 像素。

        public int m_UseWhatDecoder = 0; //存放 use 什么 Encoder ， for 0表示YU12 Raw data ， for 1表示OpenH264解码器。

        OpenH264Decoder m_OpenH264DecoderPt; //存放OpenH264解码器类对象的内存指针。
        int m_OpenH264DecoderDecodeThreadNum; //存放OpenH264解码器的解码线程 number ， unit  for 个， for 0表示直接 в 调用线程解码， for 1或2或3表示解码子线程的 number 量。

        HTSurfaceView m_VideoOutputDisplaySurfaceViewPt; //存放video Output 显示SurfaceView类对象的内存指针。
        float m_VideoOutputDisplayScale = 1.0f; //存放video Output  Display zoom  Times number 。
        int m_VideoOutputDeviceIsBlack = 0; //存放video Output device 是否 Black screen， for 0表示 Have图像， for 非0表示 Black screen。

        public LinkedList< VideoOutputFrameElm > m_VideoOutputFrameLnkLstPt; //存放video Output  frame Linked list类对象的内存指针。
        public LinkedList< VideoOutputFrameElm > m_VideoOutputIdleFrameLnkLstPt; //存放video Output 空闲 frame Linked list类对象的内存指针。

        VideoOutputThread m_VideoOutputThreadPt; //存放video Output 线程类对象的内存指针。
    }
    public class VideoOutputFrameElm //video Output  frame Linked list元素类。
    {
        VideoOutputFrameElm()
        {
            m_VideoOutputFramePt = ( m_VideoOutputPt.m_IsUseVideoOutput != 0 ) ? new byte[ m_VideoOutputPt.m_FrameWidth * m_VideoOutputPt.m_FrameHeight * 3 / 2 ] : null;
            m_VideoOutputFrameLen = 0;
        }
        byte m_VideoOutputFramePt[];
        long m_VideoOutputFrameLen;
    }

    public VideoOutput m_VideoOutputPt = new VideoOutput(); //存放video Output 类对象的内存指针。

    // user 定义的相关函 number 。
    public abstract int UserInit(); // user 定义的初始化函 number ， в this 线程刚启动 Time 回调一次，返回值表示是否成功， for 0表示成功， for 非0表示失败。

    public abstract int UserProcess(); // user 定义的处理函 number ， в this 线程运行 Time 每隔1 millisecond就回调一次，返回值表示是否成功， for 0表示成功， for 非0表示失败。

    public abstract void UserDestroy(); // user 定义的destroy函 number ， в this 线程退出 Time 回调一次。

    public abstract int UserReadAudioVideoInputFrame( short PcmAudioInputFramePt[], short PcmAudioResultFramePt[], HTInt VoiceActStsPt, byte EncoderAudioInputFramePt[], HTLong EncoderAudioInputFrameLenPt, HTInt EncoderAudioInputFrameIsNeedTransPt, byte YU12VideoInputFramePt[], HTInt YU12VideoInputFrameWidthPt, HTInt YU12VideoInputFrameHeigthPt, byte EncoderVideoInputFramePt[], HTLong EncoderVideoInputFrameLenPt ); // user 定义的读取soundvideo enter  frame 函 number ， в 读取到一个 Audio  enter  frame 或video enter  frame 并处理完后回调一次， for 0表示成功， for 非0表示失败。

    public abstract void UserWriteAudioOutputFrame( short PcmAudioOutputFramePt[], byte EncoderAudioOutputFramePt[], HTLong EncoderAudioOutputFrameLen ); // user 定义的写入 Audio  Output  frame 函 number ， в 需要写入一个 Audio  Output  frame  Time 回调一次。注意：this 函 number 不是 в 媒体处理线程in执行的，而是 в  Audio  Output 线程in执行的，所以this 函 number 应尽量 в 一瞬间完成执行，否则会导致 Audio  enter  Output  frame 不同步，从而导致 Acoustic echo sound消除失败。

    public abstract void UserGetPcmAudioOutputFrame( short PcmAudioOutputFramePt[] ); // user 定义的获取PCM格式 Audio  Output  frame 函 number ， в 解码完一个已编码 Audio  Output  frame  Time 回调一次。注意：this 函 number 不是 в 媒体处理线程in执行的，而是 в  Audio  Output 线程in执行的，所以this 函 number 应尽量 в 一瞬间完成执行，否则会导致 Audio  enter  Output  frame 不同步，从而导致 Acoustic echo sound消除失败。

    public void UserWriteVideoOutputFrame( byte VideoOutputFramePt[], int VideoOutputFrameStart, long VideoOutputFrameLen ) // user 调用的写入video Output  frame 函 number ， в  user 需要显示一个video Output  frame  Time 主调一次。
    {
        VideoOutputFrameElm p_VideoOutputFrameElmPt = null;
        int p_TmpInt32;

        out:
        {
            //写入一个video Output  frame 到video Output  frame Linked list。
            if( m_VideoOutputPt.m_VideoOutputIdleFrameLnkLstPt != null && VideoOutputFramePt != null && VideoOutputFrameLen > 0 && m_RunFlag == RUN_FLAG_PROC ) //如果要 use video Output  frame Linked list，且video Output  frame  Have图像 live动，且媒体处理线程初始化完毕正 в 循环处理 frame 。
            {
                //获取一个video Output 空闲 frame 。
                if( ( p_TmpInt32 = m_VideoOutputPt.m_VideoOutputIdleFrameLnkLstPt.size() ) > 0 ) //如果video Output 空闲 frame Linked listin Havevideo Output 空闲 frame 。
                {
                    //从video Output 空闲 frame Linked listin取出第一个video Output 空闲 frame 。
                    synchronized( m_VideoOutputPt.m_VideoOutputIdleFrameLnkLstPt )
                    {
                        p_VideoOutputFrameElmPt = m_VideoOutputPt.m_VideoOutputIdleFrameLnkLstPt.getFirst();
                        m_VideoOutputPt.m_VideoOutputIdleFrameLnkLstPt.removeFirst();
                    }
                    if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "媒体处理线程：从video Output 空闲 frame Linked listin取出第一个video Output 空闲 frame ，video Output 空闲 frame Linked list元素个 number ：" + p_TmpInt32 + "。" );
                }
                else //如果video Output 空闲 frame Linked listin没 Havevideo Output 空闲 frame 。
                {
                    if( ( p_TmpInt32 = m_VideoOutputPt.m_VideoOutputFrameLnkLstPt.size() ) <= 20 )
                    {
                        p_VideoOutputFrameElmPt = new VideoOutputFrameElm(); //创建一个video Output 空闲 frame 。
                        if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "媒体处理线程：video Output 空闲 frame Linked listin没 Havevideo Output 空闲 frame ，创建一个video Output 空闲 frame 。" );
                    }
                    else
                    {
                        if( m_IsPrintLogcat != 0 ) Log.e( m_CurClsNameStrPt, "媒体处理线程：video Output  frame Linked listinvideo Output  frame  number 量 for " + p_TmpInt32 + "已经ultra过上限20，不再创建一个video Output 空闲 frame 。" );
                        break out;
                    }
                }

                //复制video Output  frame 。
                System.arraycopy( VideoOutputFramePt, VideoOutputFrameStart, p_VideoOutputFrameElmPt.m_VideoOutputFramePt, 0, ( int ) VideoOutputFrameLen );
                p_VideoOutputFrameElmPt.m_VideoOutputFrameLen = VideoOutputFrameLen;

                //追加this 次video Output  frame 到video Output  frame Linked list。
                synchronized( m_VideoOutputPt.m_VideoOutputFrameLnkLstPt )
                {
                    m_VideoOutputPt.m_VideoOutputFrameLnkLstPt.addLast( p_VideoOutputFrameElmPt );
                }
            }
        }
    }

    //构造函 number 。
    public MediaProcThread( Context AppContextPt )
    {
        m_AppContextPt = AppContextPt; //Настраивать应用程序上下文类对象的内存指针。
    }

    //Настраивать是否 Save Настраивать到file。
    public void SetIsSaveSettingToFile( int IsSaveSettingToFile, String SettingFileFullPathStrPt )
    {
        m_IsSaveSettingToFile = IsSaveSettingToFile;
        m_SettingFileFullPathStrPt = SettingFileFullPathStrPt;
    }

    //Настраивать是否 print LogcatLog。
    public void SetIsPrintLogcat( int IsPrintLogcat )
    {
        m_IsPrintLogcat = IsPrintLogcat;
    }

    //Настраивать是否 Use wake lock 。
    public void SetIsUseWakeLock( int IsUseWakeLock )
    {
        m_IsUseWakeLock = IsUseWakeLock;

        if( m_RunFlag == RUN_FLAG_INIT || m_RunFlag == RUN_FLAG_PROC ) //如果this 线程 for 刚开始运行正 в 初始化或初始化完毕正 в 循环处理 frame ，就立即修改唤醒锁。
        {
            WakeLockInitOrDestroy( IsUseWakeLock );
        }
    }

    //初始化或destroy唤醒锁。
    void WakeLockInitOrDestroy( int IsInitOrDestroy )
    {
        if( IsInitOrDestroy != 0 ) //如果要初始化唤醒锁。
        {
            if( m_AudioOutputPt.m_IsUseAudioOutput != 0 && m_AudioOutputPt.m_UseWhatAudioOutputDevice != 0 ) //如果要 use  Audio  Output ，且要 use earpiece Audio  Output device ，就要 use 接近息屏唤醒锁。
            {
                if( m_ProximityScreenOffWakeLockPt == null ) //如果接近息屏唤醒锁类对象还没 Have初始化。
                {
                    m_ProximityScreenOffWakeLockPt = ( ( PowerManager ) m_AppContextPt.getSystemService( Activity.POWER_SERVICE ) ).newWakeLock( PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, m_CurClsNameStrPt );
                    if( m_ProximityScreenOffWakeLockPt != null )
                    {
                        m_ProximityScreenOffWakeLockPt.acquire();
                        if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "媒体处理线程：创建并初始化接近息屏唤醒锁类对象success." );
                    }
                    else
                    {
                        if( m_IsPrintLogcat != 0 ) Log.e( m_CurClsNameStrPt, "媒体处理线程：创建并初始化接近息屏唤醒锁类对象失败。" );
                    }
                }
            }
            else //如果 Do not use  Audio  Output ，或 Do not use earpiece Audio  Output device ，就 Do not use 接近息屏唤醒锁。
            {
                if( m_ProximityScreenOffWakeLockPt != null )
                {
                    try
                    {
                        m_ProximityScreenOffWakeLockPt.release();
                    }
                    catch( RuntimeException ignored )
                    {
                    }
                    m_ProximityScreenOffWakeLockPt = null;
                    if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "媒体处理线程：destroy接近息屏唤醒锁类对象success." );
                }
            }

            if( m_FullWakeLockPt == null ) //如果屏幕键盘全亮唤醒锁类对象还没 Have初始化。
            {
                m_FullWakeLockPt = ( ( PowerManager ) m_AppContextPt.getSystemService( Activity.POWER_SERVICE ) ).newWakeLock( PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, m_CurClsNameStrPt );
                if( m_FullWakeLockPt != null )
                {
                    m_FullWakeLockPt.acquire();
                    if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "媒体处理线程：创建并初始化屏幕键盘全亮唤醒锁类对象success." );
                }
                else
                {
                    if( m_IsPrintLogcat != 0 ) Log.e( m_CurClsNameStrPt, "媒体处理线程：创建并初始化屏幕键盘全亮唤醒锁类对象失败。" );
                }
            }
        }
        else //如果要destroy唤醒锁。
        {
            //destroy唤醒锁。
            if( m_ProximityScreenOffWakeLockPt != null )
            {
                try
                {
                    m_ProximityScreenOffWakeLockPt.release();
                }
                catch( RuntimeException ignored )
                {
                }
                m_ProximityScreenOffWakeLockPt = null;
                if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "媒体处理线程：destroy接近息屏唤醒锁类对象success." );
            }
            if( m_FullWakeLockPt != null )
            {
                try
                {
                    m_FullWakeLockPt.release();
                }
                catch( RuntimeException ignored )
                {
                }
                m_FullWakeLockPt = null;
                if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "媒体处理线程：destroy屏幕键盘全亮唤醒锁类对象success." );
            }
        }
    }

    //Настраивать是否 use  Audio  enter 。
    public void SetIsUseAudioInput( int IsUseAudioInput, int SamplingRate, int FrameLenMsec )
    {
        if( ( ( IsUseAudioInput != 0 ) && ( ( SamplingRate != 8000 ) && ( SamplingRate != 16000 ) && ( SamplingRate != 32000 ) ) ) || //如果 Sampling frequency 不正确。
            ( ( IsUseAudioInput != 0 ) && ( ( FrameLenMsec <= 0 ) || ( FrameLenMsec % 10 != 0 ) ) ) ) //如果 frame 的 Millisecond length 不正确。
        {
            return;
        }

        m_AudioInputPt.m_IsUseAudioInput = IsUseAudioInput;
        m_AudioInputPt.m_SamplingRate = SamplingRate;
        m_AudioInputPt.m_FrameLen = FrameLenMsec * SamplingRate / 1000;
    }

    //Настраивать Audio  enter 是否 Use the acoustic feedback that comes with the system sound Canceller, noise sound Suppressor and automatic gain controller （ The system does not necessarily come with it ）。
    public void SetAudioInputIsUseSystemAecNsAgc( int IsUseSystemAecNsAgc )
    {
        m_AudioInputPt.m_IsUseSystemAecNsAgc = IsUseSystemAecNsAgc;
    }

    //Настраивать Audio  enter  Do not use  Acoustic echo sound Eliminator 。
    public void SetAudioInputUseNoAec()
    {
        m_AudioInputPt.m_UseWhatAec = 0;
    }

    //Настраивать Audio  enter 要 use Speex Acoustic echo sound Eliminator 。
    public void SetAudioInputUseSpeexAec( int FilterLen, int IsUseRec, float EchoMultiple, float EchoCont, int EchoSupes, int EchoSupesActive, int IsSaveMemFile, String MemFileFullPathStrPt )
    {
        m_AudioInputPt.m_UseWhatAec = 1;
        m_AudioInputPt.m_SpeexAecFilterLen = FilterLen;
        m_AudioInputPt.m_SpeexAecIsUseRec = IsUseRec;
        m_AudioInputPt.m_SpeexAecEchoMultiple = EchoMultiple;
        m_AudioInputPt.m_SpeexAecEchoCont = EchoCont;
        m_AudioInputPt.m_SpeexAecEchoSupes = EchoSupes;
        m_AudioInputPt.m_SpeexAecEchoSupesAct = EchoSupesActive;
        m_AudioInputPt.m_SpeexAecIsSaveMemFile = IsSaveMemFile;
        m_AudioInputPt.m_SpeexAecMemFileFullPathStrPt = MemFileFullPathStrPt;
    }

    //Настраивать Audio  enter 要 use WebRtc Fixed-point version  Acoustic echo sound Eliminator 。
    public void SetAudioInputUseWebRtcAecm( int IsUseCNGMode, int EchoMode, int Delay )
    {
        m_AudioInputPt.m_UseWhatAec = 2;
        m_AudioInputPt.m_WebRtcAecmIsUseCNGMode = IsUseCNGMode;
        m_AudioInputPt.m_WebRtcAecmEchoMode = EchoMode;
        m_AudioInputPt.m_WebRtcAecmDelay = Delay;
    }

    //Настраивать Audio  enter 要 use WebRtc Floating point version  Acoustic echo sound Eliminator 。
    public void SetAudioInputUseWebRtcAec( int EchoMode, int Delay, int IsUseDelayAgnosticMode, int IsUseExtdFilterMode, int IsUseRefinedFilterAdaptAecMode, int IsUseAdaptAdjDelay, int IsSaveMemFile, String MemFileFullPathStrPt )
    {
        m_AudioInputPt.m_UseWhatAec = 3;
        m_AudioInputPt.m_WebRtcAecEchoMode = EchoMode;
        m_AudioInputPt.m_WebRtcAecDelay = Delay;
        m_AudioInputPt.m_WebRtcAecIsUseDelayAgnosticMode = IsUseDelayAgnosticMode;
        m_AudioInputPt.m_WebRtcAecIsUseExtdFilterMode = IsUseExtdFilterMode;
        m_AudioInputPt.m_WebRtcAecIsUseRefinedFilterAdaptAecMode = IsUseRefinedFilterAdaptAecMode;
        m_AudioInputPt.m_WebRtcAecIsUseAdaptAdjDelay = IsUseAdaptAdjDelay;
        m_AudioInputPt.m_WebRtcAecIsSaveMemFile = IsSaveMemFile;
        m_AudioInputPt.m_WebRtcAecMemFileFullPathStrPt = MemFileFullPathStrPt;
    }

    //Настраивать Audio  enter 要 use SpeexWebRtc triple  Acoustic echo sound Eliminator 。
    public void SetAudioInputUseSpeexWebRtcAec( int WorkMode, int SpeexAecFilterLen, int SpeexAecIsUseRec, float SpeexAecEchoMultiple, float SpeexAecEchoCont, int SpeexAecEchoSuppress, int SpeexAecEchoSuppressActive, int WebRtcAecmIsUseCNGMode, int WebRtcAecmEchoMode, int WebRtcAecmDelay, int WebRtcAecEchoMode, int WebRtcAecDelay, int WebRtcAecIsUseDelayAgnosticMode, int WebRtcAecIsUseExtdFilterMode, int WebRtcAecIsUseRefinedFilterAdaptAecMode, int WebRtcAecIsUseAdaptAdjDelay, int IsUseSameRoomAec, int SameRoomEchoMinDelay )
    {
        m_AudioInputPt.m_UseWhatAec = 4;
        m_AudioInputPt.m_SpeexWebRtcAecWorkMode = WorkMode;
        m_AudioInputPt.m_SpeexWebRtcAecSpeexAecFilterLen = SpeexAecFilterLen;
        m_AudioInputPt.m_SpeexWebRtcAecSpeexAecIsUseRec = SpeexAecIsUseRec;
        m_AudioInputPt.m_SpeexWebRtcAecSpeexAecEchoMultiple = SpeexAecEchoMultiple;
        m_AudioInputPt.m_SpeexWebRtcAecSpeexAecEchoCont = SpeexAecEchoCont;
        m_AudioInputPt.m_SpeexWebRtcAecSpeexAecEchoSupes = SpeexAecEchoSuppress;
        m_AudioInputPt.m_SpeexWebRtcAecSpeexAecEchoSupesAct = SpeexAecEchoSuppressActive;
        m_AudioInputPt.m_SpeexWebRtcAecWebRtcAecmIsUseCNGMode = WebRtcAecmIsUseCNGMode;
        m_AudioInputPt.m_SpeexWebRtcAecWebRtcAecmEchoMode = WebRtcAecmEchoMode;
        m_AudioInputPt.m_SpeexWebRtcAecWebRtcAecmDelay = WebRtcAecmDelay;
        m_AudioInputPt.m_SpeexWebRtcAecWebRtcAecEchoMode = WebRtcAecEchoMode;
        m_AudioInputPt.m_SpeexWebRtcAecWebRtcAecDelay = WebRtcAecDelay;
        m_AudioInputPt.m_SpeexWebRtcAecWebRtcAecIsUseDelayAgnosticMode = WebRtcAecIsUseDelayAgnosticMode;
        m_AudioInputPt.m_SpeexWebRtcAecWebRtcAecIsUseExtdFilterMode = WebRtcAecIsUseExtdFilterMode;
        m_AudioInputPt.m_SpeexWebRtcAecWebRtcAecIsUseRefinedFilterAdaptAecMode = WebRtcAecIsUseRefinedFilterAdaptAecMode;
        m_AudioInputPt.m_SpeexWebRtcAecWebRtcAecIsUseAdaptAdjDelay = WebRtcAecIsUseAdaptAdjDelay;
        m_AudioInputPt.m_SpeexWebRtcAecIsUseSameRoomAec = IsUseSameRoomAec;
        m_AudioInputPt.m_SpeexWebRtcAecSameRoomEchoMinDelay = SameRoomEchoMinDelay;
    }

    //Настраивать Audio  enter  Do not use  noise sound Suppressor 。
    public void SetAudioInputUseNoNs()
    {
        m_AudioInputPt.m_UseWhatNs = 0;
    }

    //Настраивать Audio  enter 要 use Speex Preprocessor  noise sound торможение 。
    public void SetAudioInputUseSpeexPprocNs( int IsUseNs, int NoiseSupes, int IsUseDereverberation )
    {
        m_AudioInputPt.m_UseWhatNs = 1;
        m_AudioInputPt.m_SpeexPprocIsUseNs = IsUseNs;
        m_AudioInputPt.m_SpeexPprocNoiseSupes = NoiseSupes;
        m_AudioInputPt.m_SpeexPprocIsUseDereverb = IsUseDereverberation;
    }

    //Настраивать Audio  enter 要 use WebRtc Fixed-point version  noise sound Suppressor 。
    public void SetAudioInputUseWebRtcNsx( int PolicyMode )
    {
        m_AudioInputPt.m_UseWhatNs = 2;
        m_AudioInputPt.m_WebRtcNsxPolicyMode = PolicyMode;
    }

    //Настраивать Audio  enter 要 use WebRtc Fixed-point version  noise sound Suppressor 。
    public void SetAudioInputUseWebRtcNs( int PolicyMode )
    {
        m_AudioInputPt.m_UseWhatNs = 3;
        m_AudioInputPt.m_WebRtcNsPolicyMode = PolicyMode;
    }

    //Настраивать Audio  enter 要 use RNNoise noise sound Suppressor 。
    public void SetAudioInputUseRNNoise()
    {
        m_AudioInputPt.m_UseWhatNs = 4;
    }

    //Настраивать Audio  enter 是否 use Speex Preprocessor  Other functions 。
    public void SetAudioInputIsUseSpeexPprocOther( int IsUseOther, int IsUseVad, int VadProbStart, int VadProbCont, int IsUseAgc, int AgcLevel, int AgcIncrement, int AgcDecrement, int AgcMaxGain )
    {
        m_AudioInputPt.m_IsUseSpeexPprocOther = IsUseOther;
        m_AudioInputPt.m_SpeexPprocIsUseVad = IsUseVad;
        m_AudioInputPt.m_SpeexPprocVadProbStart = VadProbStart;
        m_AudioInputPt.m_SpeexPprocVadProbCont = VadProbCont;
        m_AudioInputPt.m_SpeexPprocIsUseAgc = IsUseAgc;
        m_AudioInputPt.m_SpeexPprocAgcIncrement = AgcIncrement;
        m_AudioInputPt.m_SpeexPprocAgcDecrement = AgcDecrement;
        m_AudioInputPt.m_SpeexPprocAgcLevel = AgcLevel;
        m_AudioInputPt.m_SpeexPprocAgcMaxGain = AgcMaxGain;
    }

    //Настраивать Audio  enter 要 use PCM Raw data 。
    public void SetAudioInputUsePcm()
    {
        m_AudioInputPt.m_UseWhatEncoder = 0;
    }

    //Настраивать Audio  enter 要 use Speex Encoder 。
    public void SetAudioInputUseSpeexEncoder( int UseCbrOrVbr, int Quality, int Complexity, int PlcExpectedLossRate )
    {
        m_AudioInputPt.m_UseWhatEncoder = 1;
        m_AudioInputPt.m_SpeexEncoderUseCbrOrVbr = UseCbrOrVbr;
        m_AudioInputPt.m_SpeexEncoderQuality = Quality;
        m_AudioInputPt.m_SpeexEncoderComplexity = Complexity;
        m_AudioInputPt.m_SpeexEncoderPlcExpectedLossRate = PlcExpectedLossRate;
    }

    //Настраивать Audio  enter 要 use Opus Encoder 。
    public void SetAudioInputUseOpusEncoder()
    {
        m_AudioInputPt.m_UseWhatEncoder = 2;
    }

    //Настраивать Audio  enter 是否 Save  Audio 到file。
    public void SetAudioInputIsSaveAudioToFile( int IsSaveAudioToFile, String AudioInputFileFullPathStrPt, String AudioResultFileFullPathStrPt )
    {
        m_AudioInputPt.m_IsSaveAudioToFile = IsSaveAudioToFile;
        m_AudioInputPt.m_AudioInputFileFullPathStrPt = AudioInputFileFullPathStrPt;
        m_AudioInputPt.m_AudioResultFileFullPathStrPt = AudioResultFileFullPathStrPt;
    }

    //Настраивать Audio input device是否 Quiet sound。
    public void SetAudioInputDeviceIsMute( int IsMute )
    {
        m_AudioInputPt.m_AudioInputDeviceIsMute = IsMute;
    }

    //Настраивать是否 use  Audio  Output 。
    public void SetIsUseAudioOutput( int IsUseAudioOutput, int SamplingRate, int FrameLenMsec )
    {
        if( ( ( IsUseAudioOutput != 0 ) && ( ( SamplingRate != 8000 ) && ( SamplingRate != 16000 ) && ( SamplingRate != 32000 ) ) ) || //如果 Sampling frequency 不正确。
            ( ( IsUseAudioOutput != 0 ) && ( ( FrameLenMsec == 0 ) || ( FrameLenMsec % 10 != 0 ) ) ) ) //如果 frame 的 Millisecond length 不正确。
        {
            return;
        }

        m_AudioOutputPt.m_IsUseAudioOutput = IsUseAudioOutput;
        m_AudioOutputPt.m_SamplingRate = SamplingRate;
        m_AudioOutputPt.m_FrameLen = FrameLenMsec * SamplingRate / 1000;
    }

    //Настраивать Audio  Output 要 use PCM Raw data 。
    public void SetAudioOutputUsePcm()
    {
        m_AudioOutputPt.m_UseWhatDecoder = 0;
    }

    //Настраивать Audio  Output 要 use Speex解码器。
    public void SetAudioOutputUseSpeexDecoder( int IsUsePerceptualEnhancement )
    {
        m_AudioOutputPt.m_UseWhatDecoder = 1;
        m_AudioOutputPt.m_SpeexDecoderIsUsePerceptualEnhancement = IsUsePerceptualEnhancement;
    }

    //Настраивать Audio  Output 要 use Opus Encoder 。
    public void SetAudioOutputUseOpusDecoder()
    {
        m_AudioOutputPt.m_UseWhatDecoder = 2;
    }

    //Настраивать Audio  Output 是否 Save  Audio 到file。
    public void SetAudioOutputIsSaveAudioToFile( int IsSaveAudioToFile, String AudioOutputFileFullPathStrPt )
    {
        m_AudioOutputPt.m_IsSaveAudioToFile = IsSaveAudioToFile;
        m_AudioOutputPt.m_AudioOutputFileFullPathStrPt = AudioOutputFileFullPathStrPt;
    }

    //Настраивать Audio  Output  use 的设备。
    public void SetAudioOutputUseDevice( int UseSpeakerOrEarpiece, int UseVoiceCallOrMusic )
    {
        if( ( UseSpeakerOrEarpiece != 0 ) && ( UseVoiceCallOrMusic != 0 ) )//如果 use earpiece，则不能 use 媒体 Types of  Audio  Output 流。
        {
            return;
        }

        m_AudioOutputPt.m_UseWhatAudioOutputDevice = UseSpeakerOrEarpiece;
        m_AudioOutputPt.m_UseWhatAudioOutputStreamType = UseVoiceCallOrMusic;
        SetIsUseWakeLock( m_IsUseWakeLock ); //重新初始化唤醒锁。
    }

    //Настраивать Audio  Output device 是否 Quiet sound。
    public void SetAudioOutputDeviceIsMute( int IsMute )
    {
        m_AudioOutputPt.m_AudioOutputDeviceIsMute = IsMute; //Настраивать Audio  Output device 是否 Quiet sound。
    }

    //Настраивать是否 use video enter 。
    public void SetIsUseVideoInput( int IsUseVideoInput, int MaxSamplingRate, int FrameWidth, int FrameHeight, HTSurfaceView VideoInputPreviewSurfaceViewPt )
    {
        if( ( ( IsUseVideoInput != 0 ) && ( ( MaxSamplingRate < 1 ) || ( MaxSamplingRate > 60 ) ) ) || //如果 Sampling frequency 不正确。
            ( ( IsUseVideoInput != 0 ) && ( FrameWidth < 1 ) ) || //如果 frame 的宽度不正确。
            ( ( IsUseVideoInput != 0 ) && ( FrameHeight < 1 ) ) || //如果 frame 的high度不正确。
            ( ( IsUseVideoInput != 0 ) && ( VideoInputPreviewSurfaceViewPt == null ) ) ) //如果video预览SurfaceView类对象的内存指针不正确。
        {
            return;
        }

        m_VideoInputPt.m_IsUseVideoInput = IsUseVideoInput;
        m_VideoInputPt.m_MaxSamplingRate = MaxSamplingRate;
        m_VideoInputPt.m_FrameWidth = FrameWidth;
        m_VideoInputPt.m_FrameHeight = FrameHeight;
        m_VideoInputPt.m_VideoInputPreviewSurfaceViewPt = VideoInputPreviewSurfaceViewPt;
    }

    //Настраиватьvideo enter 要 use YU12 Raw data 。
    public void SetVideoInputUseYU12()
    {
        m_VideoInputPt.m_UseWhatEncoder = 0;
    }

    //Настраиватьvideo enter 要 use OpenH264 Encoder 。
    public void SetVideoInputUseOpenH264Encoder( int VideoType, int EncodedBitrate, int BitrateControlMode, int IDRFrameIntvl, int Complexity )
    {
        m_VideoInputPt.m_UseWhatEncoder = 1;
        m_VideoInputPt.m_OpenH264EncoderVideoType = VideoType;
        m_VideoInputPt.m_OpenH264EncoderEncodedBitrate = EncodedBitrate;
        m_VideoInputPt.m_OpenH264EncoderBitrateControlMode = BitrateControlMode;
        m_VideoInputPt.m_OpenH264EncoderIDRFrameIntvl = IDRFrameIntvl;
        m_VideoInputPt.m_OpenH264EncoderComplexity = Complexity;
    }

    //Настраиватьvideo enter  use 的设备。
    public void SetVideoInputUseDevice( int UseFrontOrBack )
    {
        m_VideoInputPt.m_UseWhatVideoInputDevice = ( UseFrontOrBack == 0 ) ? Camera.CameraInfo.CAMERA_FACING_FRONT : Camera.CameraInfo.CAMERA_FACING_BACK; //Настраиватьvideoinput device。
    }

    //Настраиватьvideoinput device是否 Black screen。
    public void SetVideoInputDeviceIsBlack( int IsBlack )
    {
        m_VideoInputPt.m_VideoInputDeviceIsBlack = IsBlack;
    }

    //Настраивать是否 use video Output 。
    public void SetIsUseVideoOutput( int IsUseVideoOutput, int FrameWidth, int FrameHeight, HTSurfaceView VideoOutputDisplaySurfaceViewPt, float VideoDisplayScale )
    {
        if( ( ( IsUseVideoOutput != 0 ) && ( FrameWidth <= 0 ) ) || //如果 frame 的宽度不正确。
            ( ( IsUseVideoOutput != 0 ) && ( FrameHeight <= 0 ) ) || //如果 frame 的high度不正确。
            ( ( IsUseVideoOutput != 0 ) && ( VideoOutputDisplaySurfaceViewPt == null ) ) || //如果video显示SurfaceView类对象的内存指针不正确。
            ( ( IsUseVideoOutput != 0 ) && ( VideoDisplayScale <= 0 ) ) ) //如果video Display zoom  Times number 不正确。
        {
            return;
        }

        m_VideoOutputPt.m_IsUseVideoOutput = IsUseVideoOutput;
        m_VideoOutputPt.m_FrameWidth = FrameWidth;
        m_VideoOutputPt.m_FrameHeight = FrameHeight;
        m_VideoOutputPt.m_VideoOutputDisplaySurfaceViewPt = VideoOutputDisplaySurfaceViewPt;
        m_VideoOutputPt.m_VideoOutputDisplayScale = VideoDisplayScale;
    }

    //Настраиватьvideo Output 要 use YU12 Raw data 。
    public void SetVideoOutputUseYU12()
    {
        m_VideoOutputPt.m_UseWhatDecoder = 0;
    }

    //Настраиватьvideo Output 要 use OpenH264解码器。
    public void SetVideoOutputUseOpenH264Decoder( int DecodeThreadNum )
    {
        m_VideoOutputPt.m_UseWhatDecoder = 1;
        m_VideoOutputPt.m_OpenH264DecoderDecodeThreadNum = DecodeThreadNum;
    }

    //Настраиватьvideo Output device 是否 Black screen。
    public void SetVideoOutputDeviceIsBlack( int IsBlack )
    {
        m_VideoOutputPt.m_VideoOutputDeviceIsBlack = IsBlack;
    }

    //请求this 线程退出。
    public int RequireExit( int ExitFlag, int IsBlockWait )
    {
        int p_Result = -1; //存放this 函 number 执行 result 的值， for 0表示成功， for 非0表示失败。

        out:
        {
            //判断各个变量是否正确。
            if( ( ExitFlag < 0 ) || ( ExitFlag > 3 ) ) //如果退出标记不正确。
            {
                break out;
            }

            m_ExitFlag = ExitFlag; //Настраивать媒体处理线程的退出标记。

            if( IsBlockWait != 0 ) //如果需要阻塞等待。
            {
                if( ExitFlag == 1 ) //如果是请求退出。
                {
                    do
                    {
                        if( this.isAlive() != true ) //如果媒体处理线程已经退出。
                        {
                            break;
                        }

                        SystemClock.sleep( 1 ); //暂停一下，避免CPU use  rate 过high。
                    }while( true );
                }
                else //如果是请求重启。
                {
                    //等待重启完毕。
                    do
                    {
                        if( this.isAlive() != true ) //如果媒体处理线程已经退出。
                        {
                            break;
                        }
                        if( m_ExitFlag == 0 ) //如果退出标记 for 0保持运行，表示重启完毕。
                        {
                            break;
                        }

                        SystemClock.sleep( 1 ); //暂停一下，避免CPU use  rate 过high。
                    }
                    while( true );
                }
            }

            p_Result = 0;
        }

        return p_Result;
    }

    // Audio  enter 线程类。
    private class AudioInputThread extends Thread
    {
        public int m_ExitFlag = 0; //this 线程退出标记，0表示保持运行，1表示请求退出。

        //请求this 线程退出。
        public void RequireExit()
        {
            m_ExitFlag = 1;
        }

        public void run()
        {
            this.setPriority( MAX_PRIORITY ); //Настраиватьthis 线程优先级。
            Process.setThreadPriority( Process.THREAD_PRIORITY_URGENT_AUDIO ); //Настраиватьthis 线程优先级。

            short p_PcmAudioInputFramePt[]; //存放PCM格式 Audio  enter  frame 。
            int p_TmpInt32;
            long p_LastMsec = 0;
            long p_NowMsec = 0;

            if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, " Audio  enter 线程：开始准备 Audio  enter 。" );

            if( m_AudioInputPt.m_UseWhatAec != 0 ) //如果要 use  Audio  enter 的 Acoustic echo sound消除，就自适应计算 Acoustic echo sound的延迟，并Настраивать到 Acoustic echo sound Eliminator 。
            {
                int p_Delay = 0; //存放 Acoustic echo sound的延迟， unit  millisecond。
                HTInt p_HTIntDelay = new HTInt();

                //计算 Audio  Output 的延迟。
                m_AudioOutputPt.m_AudioOutputDevicePt.play(); //让 Audio  Output device 类对象开始播放。
                p_PcmAudioInputFramePt = new short[ m_AudioOutputPt.m_FrameLen ]; //创建一个空的 Audio  Output  frame 。
                p_LastMsec = System.currentTimeMillis();
                while( true )
                {
                    m_AudioOutputPt.m_AudioOutputDevicePt.write( p_PcmAudioInputFramePt, 0, p_PcmAudioInputFramePt.length ); //播放一个空的 Audio  Output  frame 。
                    p_NowMsec = System.currentTimeMillis();
                    p_Delay += m_AudioOutputPt.m_FrameLen; //递增 Audio  Output 的延迟。
                    if( p_NowMsec - p_LastMsec >= 10 ) //如果播放耗 Time 较长，就表示 Audio  Output 类对象的缓冲区已经写满，结束计算。
                    {
                        break;
                    }
                    p_LastMsec = p_NowMsec;
                }
                p_Delay = p_Delay * 1000 / m_AudioOutputPt.m_SamplingRate; // will  Audio  Output 的延迟转换 for  millisecond。
                if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, " Audio  enter 线程：" + " Audio  Output 延迟：" + p_Delay + "  millisecond。" );

                //计算 Audio  enter 的延迟。
                m_AudioInputPt.m_AudioInputDevicePt.startRecording(); //让 Audio input device类对象开始录sound。
                p_PcmAudioInputFramePt = new short[ m_AudioInputPt.m_FrameLen ]; //创建一个空的 Audio  enter  frame 。
                p_LastMsec = System.currentTimeMillis();
                m_AudioInputPt.m_AudioInputDevicePt.read( p_PcmAudioInputFramePt, 0, p_PcmAudioInputFramePt.length ); //计算读取一个 Audio  enter  frame 的耗 Time 。
                p_NowMsec = System.currentTimeMillis();
                if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, " Audio  enter 线程：" + " Audio  enter 延迟：" + ( p_NowMsec - p_LastMsec ) + "  millisecond。" );

                m_AudioOutputPt.m_AudioOutputThreadPt.start(); //启动 Audio  Output 线程。

                //计算 Acoustic echo sound的延迟。
                p_Delay = p_Delay + ( int ) ( p_NowMsec - p_LastMsec );
                if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, " Audio  enter 线程：" + " Acoustic echo sound延迟：" + p_Delay + "  millisecond，现 в 启动 Audio  Output 线程，并开始 Audio  enter 循环， for 了保证 Audio  enter 线程走 в  Output  number 据线程的前面。" );

                //Настраивать到WebRtc Fixed-point version 和 Floating point version  Acoustic echo sound Eliminator 。
                if( ( m_AudioInputPt.m_WebRtcAecmPt != null ) && ( m_AudioInputPt.m_WebRtcAecmPt.GetDelay( p_HTIntDelay ) == 0 ) && ( p_HTIntDelay.m_Val == 0 ) ) //如果要 use WebRtc Fixed-point version  Acoustic echo sound Eliminator ，且需要自适应Настраивать Return to sound delay 。
                {
                    m_AudioInputPt.m_WebRtcAecmPt.SetDelay( p_Delay / 2 );
                    m_AudioInputPt.m_WebRtcAecmPt.GetDelay( p_HTIntDelay );
                    if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, " Audio  enter 线程：自适应НастраиватьWebRtc Fixed-point version  Acoustic echo sound Eliminator 的回sound延迟 for  " + p_HTIntDelay.m_Val + "  millisecond。" );
                }
                if( ( m_AudioInputPt.m_WebRtcAecPt != null ) && ( m_AudioInputPt.m_WebRtcAecPt.GetDelay( p_HTIntDelay ) == 0 ) && ( p_HTIntDelay.m_Val == 0 ) ) //如果要 use WebRtc Floating point version  Acoustic echo sound Eliminator ，且需要自适应Настраивать Return to sound delay 。
                {
                    if( m_AudioInputPt.m_WebRtcAecIsUseDelayAgnosticMode == 0 ) //如果WebRtc Floating point version  Acoustic echo sound Eliminator  Do not use 回sound延迟不可知模式。
                    {
                        m_AudioInputPt.m_WebRtcAecPt.SetDelay( p_Delay );
                        m_AudioInputPt.m_WebRtcAecPt.GetDelay( p_HTIntDelay );
                    }
                    else //如果WebRtc Floating point version  Acoustic echo sound Eliminator 要 use 回sound延迟不可知模式。
                    {
                        m_AudioInputPt.m_WebRtcAecPt.SetDelay( 20 );
                        m_AudioInputPt.m_WebRtcAecPt.GetDelay( p_HTIntDelay );
                    }
                    if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, " Audio  enter 线程：自适应НастраиватьWebRtc Floating point version  Acoustic echo sound Eliminator 的回sound延迟 for  " + p_HTIntDelay.m_Val + "  millisecond。" );
                }
                if( ( m_AudioInputPt.m_SpeexWebRtcAecPt != null ) && ( m_AudioInputPt.m_SpeexWebRtcAecPt.GetWebRtcAecmDelay( p_HTIntDelay ) == 0 ) && ( p_HTIntDelay.m_Val == 0 ) ) //如果要 use SpeexWebRtc triple  Acoustic echo sound Eliminator ，且WebRtc Fixed-point version  Acoustic echo sound Eliminator 需要自适应Настраивать Return to sound delay 。
                {
                    m_AudioInputPt.m_SpeexWebRtcAecPt.SetWebRtcAecmDelay( p_Delay / 2 );
                    m_AudioInputPt.m_SpeexWebRtcAecPt.GetWebRtcAecmDelay( p_HTIntDelay );
                    if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, " Audio  enter 线程：自适应НастраиватьSpeexWebRtc triple  Acoustic echo sound Eliminator 的WebRtc Fixed-point version  Acoustic echo sound Eliminator 的回sound延迟 for  " + p_HTIntDelay.m_Val + "  millisecond。" );
                }
                if( ( m_AudioInputPt.m_SpeexWebRtcAecPt != null ) && ( m_AudioInputPt.m_SpeexWebRtcAecPt.GetWebRtcAecDelay( p_HTIntDelay ) == 0 ) && ( p_HTIntDelay.m_Val == 0 ) ) //如果要 use SpeexWebRtc triple  Acoustic echo sound Eliminator ，且WebRtc Floating point version  Acoustic echo sound Eliminator 需要自适应Настраивать Return to sound delay 。
                {
                    if( m_AudioInputPt.m_SpeexWebRtcAecWebRtcAecIsUseDelayAgnosticMode == 0 ) //如果SpeexWebRtc triple  Acoustic echo sound Eliminator 的WebRtc Floating point version  Acoustic echo sound Eliminator  Do not use 回sound延迟不可知模式。
                    {
                        m_AudioInputPt.m_SpeexWebRtcAecPt.SetWebRtcAecDelay( p_Delay );
                        m_AudioInputPt.m_SpeexWebRtcAecPt.GetWebRtcAecDelay( p_HTIntDelay );
                    }
                    else //如果SpeexWebRtc triple  Acoustic echo sound Eliminator 的WebRtc Floating point version  Acoustic echo sound Eliminator 要 use 回sound延迟不可知模式。
                    {
                        m_AudioInputPt.m_SpeexWebRtcAecPt.SetWebRtcAecDelay( 20 );
                        m_AudioInputPt.m_SpeexWebRtcAecPt.GetWebRtcAecDelay( p_HTIntDelay );
                    }
                    if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, " Audio  enter 线程：自适应НастраиватьSpeexWebRtc triple  Acoustic echo sound Eliminator 的WebRtc Floating point version  Acoustic echo sound Eliminator 的回sound延迟 for  " + p_HTIntDelay.m_Val + "  millisecond。" );
                }
            }
            else //如果 Do not use  Audio  enter 的 Acoustic echo sound消除，就直接启动 Audio  Output 线程。
            {
                m_AudioInputPt.m_AudioInputDevicePt.startRecording(); //让 Audio input device类对象开始录sound。
                if( m_AudioOutputPt.m_IsUseAudioOutput != 0 ) //如果要 use  Audio  Output 。
                {
                    m_AudioOutputPt.m_AudioOutputDevicePt.play(); //让 Audio  Output device 类对象开始播放。
                    m_AudioOutputPt.m_AudioOutputThreadPt.start(); //启动 Audio  Output 线程。
                }
            }

            //开始 Audio  enter 循环。
            out:
            while( true )
            {
                //获取一个 Audio  enter 空闲 frame 。
                if( ( p_TmpInt32 = m_AudioInputPt.m_AudioInputIdleFrameLnkLstPt.size() ) > 0 ) //如果 Audio  enter 空闲 frame Linked listin Have Audio  enter 空闲 frame 。
                {
                    //从 Audio  enter 空闲 frame Linked listin取出第一个 Audio  enter 空闲 frame 。
                    synchronized( m_AudioInputPt.m_AudioInputIdleFrameLnkLstPt )
                    {
                        p_PcmAudioInputFramePt = m_AudioInputPt.m_AudioInputIdleFrameLnkLstPt.getFirst();
                        m_AudioInputPt.m_AudioInputIdleFrameLnkLstPt.removeFirst();
                    }
                    if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, " Audio  enter 线程：从 Audio  enter 空闲 frame Linked listin取出第一个 Audio  enter 空闲 frame ， Audio  enter 空闲 frame Linked list元素个 number ：" + p_TmpInt32 + "。" );
                }
                else //如果 Audio  enter 空闲 frame Linked listin没 Have Audio  enter 空闲 frame 。
                {
                    if( ( p_TmpInt32 = m_AudioInputPt.m_AudioInputFrameLnkLstPt.size() ) <= 50 )
                    {
                        p_PcmAudioInputFramePt = new short[m_AudioInputPt.m_FrameLen]; //创建一个 Audio  enter 空闲 frame 。
                        if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, " Audio  enter 线程： Audio  enter 空闲 frame Linked listin没 Have Audio  enter 空闲 frame ，创建一个 Audio  enter 空闲 frame 。" );
                    }
                    else
                    {
                        p_PcmAudioInputFramePt = null;
                        if( m_IsPrintLogcat != 0 ) Log.e( m_CurClsNameStrPt, " Audio  enter 线程： Audio  enter  frame Linked listin Audio  enter  frame  number 量 for " + p_TmpInt32 + "已经ultra过上限50，不再创建一个 Audio  enter 空闲 frame 。" );
                    }
                }

                if( p_PcmAudioInputFramePt != null ) //如果获取了一个 Audio  enter 空闲 frame 。
                {
                    if( m_IsPrintLogcat != 0 ) p_LastMsec = System.currentTimeMillis();

                    //读取this 次 Audio  enter  frame 。
                    m_AudioInputPt.m_AudioInputDevicePt.read( p_PcmAudioInputFramePt, 0, p_PcmAudioInputFramePt.length );

                    //追加this 次 Audio  enter  frame 到 Audio  enter  frame Linked list。
                    synchronized( m_AudioInputPt.m_AudioInputFrameLnkLstPt )
                    {
                        m_AudioInputPt.m_AudioInputFrameLnkLstPt.addLast( p_PcmAudioInputFramePt );
                    }

                    if( m_IsPrintLogcat != 0 )
                    {
                        p_NowMsec = System.currentTimeMillis();
                        Log.i( m_CurClsNameStrPt, " Audio  enter 线程：this 次 Audio  enter  frame 读取完毕，耗 Time  " + ( p_NowMsec - p_LastMsec ) + "  millisecond。" );
                    }
                }

                if( m_ExitFlag == 1 ) //如果退出标记 for 请求退出。
                {
                    if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, " Audio  enter 线程：this 线程接收到退出请求，开始准备退出。" );
                    break out;
                }
            } // Audio  enter 循环完毕。

            if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, " Audio  enter 线程：this 线程已退出。" );
        }
    }

    // Audio  Output 线程类。
    private class AudioOutputThread extends Thread
    {
        public int m_ExitFlag = 0; //this 线程退出标记，0表示保持运行，1表示请求退出。

        //请求this 线程退出。
        public void RequireExit()
        {
            m_ExitFlag = 1;
        }

        public void run()
        {
            this.setPriority( MAX_PRIORITY ); //Настраиватьthis 线程优先级。
            Process.setThreadPriority( Process.THREAD_PRIORITY_URGENT_AUDIO ); //Настраиватьthis 线程优先级。

            short p_PcmAudioOutputFramePt[]; //存放PCM格式 Audio  Output  frame 。
            byte p_EncoderAudioOutputFramePt[] = ( m_AudioOutputPt.m_UseWhatDecoder != 0 ) ? new byte[ m_AudioOutputPt.m_FrameLen ] : null; //存放已编码格式 Audio  Output  frame 。
            HTLong p_EncoderAudioOutputFrameLenPt = ( m_AudioOutputPt.m_UseWhatDecoder != 0 ) ? new HTLong() : null; //存放已编码格式 Audio  Output  frame 的 number According to the length ， unit digit节。
            long p_LastMsec = 0;
            long p_NowMsec = 0;
            int p_TmpInt32;

            if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, " Audio  Output 线程：开始准备 Audio  Output 。" );

            //开始 Audio  Output 循环。
            out:
            while( true )
            {
                //获取一个 Audio  Output 空闲 frame 。
                if( ( p_TmpInt32 = m_AudioOutputPt.m_AudioOutputIdleFrameLnkLstPt.size() ) > 0 ) //如果 Audio  Output 空闲 frame Linked listin Have Audio  Output 空闲 frame 。
                {
                    //从 Audio  Output 空闲 frame Linked listin取出第一个 Audio  Output 空闲 frame 。
                    synchronized( m_AudioOutputPt.m_AudioOutputIdleFrameLnkLstPt )
                    {
                        p_PcmAudioOutputFramePt = m_AudioOutputPt.m_AudioOutputIdleFrameLnkLstPt.getFirst();
                        m_AudioOutputPt.m_AudioOutputIdleFrameLnkLstPt.removeFirst();
                    }
                    if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, " Audio  Output 线程：从 Audio  Output 空闲 frame Linked listin取出第一个 Audio  Output 空闲 frame ， Audio  Output 空闲 frame Linked list元素个 number ：" + p_TmpInt32 + "。" );
                }
                else //如果 Audio  Output 空闲 frame Linked listin没 Have Audio  Output 空闲 frame 。
                {
                    if( ( p_TmpInt32 = m_AudioOutputPt.m_AudioOutputFrameLnkLstPt.size() ) <= 50 )
                    {
                        p_PcmAudioOutputFramePt = new short[m_AudioOutputPt.m_FrameLen]; //创建一个 Audio  Output 空闲 frame 。
                        if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, " Audio  Output 线程： Audio  Output 空闲 frame Linked listin没 Have Audio  Output 空闲 frame ，创建一个 Audio  Output 空闲 frame 。" );
                    }
                    else
                    {
                        p_PcmAudioOutputFramePt = null;
                        if( m_IsPrintLogcat != 0 ) Log.e( m_CurClsNameStrPt, " Audio  Output 线程： Audio  Output  frame Linked listin Audio  Output  frame  number 量 for " + p_TmpInt32 + "已经ultra过上限50，不再创建一个 Audio  Output 空闲 frame 。" );
                    }
                }

                if( p_PcmAudioOutputFramePt != null ) //如果获取了一个 Audio  Output 空闲 frame 。
                {
                    if( m_IsPrintLogcat != 0 ) p_LastMsec = System.currentTimeMillis();

                    //调用 user 定义的写入 Audio  Output  frame 函 number ，并解码成PCM Raw data 。
                    switch( m_AudioOutputPt.m_UseWhatDecoder ) // use 什么解码器。
                    {
                        case 0: //如果要 use PCM Raw data 。
                        {
                            //调用 user 定义的写入 Audio  Output  frame 函 number 。
                            UserWriteAudioOutputFrame( p_PcmAudioOutputFramePt, null, null );
                            break;
                        }
                        case 1: //如果要 use Speex解码器。
                        {
                            //调用 user 定义的写入 Audio  Output  frame 函 number 。
                            UserWriteAudioOutputFrame( null, p_EncoderAudioOutputFramePt, p_EncoderAudioOutputFrameLenPt );

                            // use Speex解码器。
                            if( m_AudioOutputPt.m_SpeexDecoderPt.Proc( p_EncoderAudioOutputFramePt, p_EncoderAudioOutputFrameLenPt.m_Val, p_PcmAudioOutputFramePt ) == 0 )
                            {
                                if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, " Audio  Output 线程： use Speex解码器success." );
                            }
                            else
                            {
                                if( m_IsPrintLogcat != 0 ) Log.e( m_CurClsNameStrPt, " Audio  Output 线程： use Speex解码器失败。" );
                            }
                            break;
                        }
                        case 2: //如果要 use Opus解码器。
                        {
                            if( m_IsPrintLogcat != 0 ) Log.e( m_CurClsNameStrPt, " Audio  Output 线程：暂不支持 use Opus解码器。" );
                        }
                    }

                    //判断 Audio  Output device 是否 Quiet sound。 в  Audio 处理完后再Настраивать Quiet sound，这样可以保证 Audio 处理器的连续性。
                    if( m_AudioOutputPt.m_AudioOutputDeviceIsMute != 0 )
                    {
                        Arrays.fill( p_PcmAudioOutputFramePt, ( short ) 0 );
                    }

                    //写入this 次 Audio  Output  frame 到 Audio  Output device 。
                    m_AudioOutputPt.m_AudioOutputDevicePt.write( p_PcmAudioOutputFramePt, 0, p_PcmAudioOutputFramePt.length );

                    //调用 user 定义的获取PCM格式 Audio  Output  frame 函 number 。
                    UserGetPcmAudioOutputFrame( p_PcmAudioOutputFramePt );

                    //追加this 次 Audio  Output  frame 到 Audio  Output  frame Linked list。
                    synchronized( m_AudioOutputPt.m_AudioOutputFrameLnkLstPt )
                    {
                        m_AudioOutputPt.m_AudioOutputFrameLnkLstPt.addLast( p_PcmAudioOutputFramePt );
                    }

                    if( m_IsPrintLogcat != 0 )
                    {
                        p_NowMsec = System.currentTimeMillis();
                        Log.i( m_CurClsNameStrPt, " Audio  Output 线程：this 次 Audio  Output  frame 写入完毕，耗 Time  " + ( p_NowMsec - p_LastMsec ) + "  millisecond。" );
                    }
                }

                if( m_ExitFlag == 1 ) //如果退出标记 for 请求退出。
                {
                    if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, " Audio  Output 线程：this 线程接收到退出请求，开始准备退出。" );
                    break out;
                }
            } // Audio  Output 循环完毕。

            if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, " Audio  Output 线程：this 线程已退出。" );
        }
    }

    //video enter 线程类。
    private class VideoInputThread extends Thread implements Camera.PreviewCallback
    {
        public int m_ExitFlag = 0; //this 线程退出标记，0表示保持运行，1表示请求退出。

        //请求this 线程退出。
        public void RequireExit()
        {
            m_ExitFlag = 1;
        }

        //读取一个video enter  frame 的预览回调函 number ，this 函 number 是 в 主线程in运行的。
        @Override
        public void onPreviewFrame( byte[] data, Camera camera )
        {
            //追加this 次video enter  frame 到video enter  frame Linked list。
            synchronized( m_VideoInputPt.m_NV21VideoInputFrameLnkLstPt )
            {
                m_VideoInputPt.m_NV21VideoInputFrameLnkLstPt.addLast( data );
            }
            if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "video enter 线程：读取一个video enter  frame 。" );
        }

        public void run()
        {
            this.setPriority( MAX_PRIORITY ); //Настраиватьthis 线程优先级。
            Process.setThreadPriority( Process.THREAD_PRIORITY_URGENT_AUDIO ); //Настраиватьthis 线程优先级。

            byte p_NV21VideoInputFramePt[] = null;
            VideoInputFrameElm p_VideoInputFrameElmPt = null;
            long p_LastMsec = 0;
            long p_NowMsec = 0;
            int p_TmpInt32;

            if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "video enter 线程：开始准备video enter 。" );

            //开始video enter 循环。
            out:
            while( true )
            {
                //处理video enter  frame 。
                if( m_VideoInputPt.m_NV21VideoInputFrameLnkLstPt.size() > 0 )//如果NV21格式video enter  frame Linked listin Have frame 了。
                {
                    //获取一个video enter 空闲 frame 。
                    if( ( p_TmpInt32 = m_VideoInputPt.m_VideoInputIdleFrameLnkLstPt.size() ) > 0 ) //如果video enter 空闲 frame Linked listin Havevideo enter 空闲 frame 。
                    {
                        //从video enter 空闲 frame Linked listin取出第一个video enter 空闲 frame 。
                        synchronized( m_VideoInputPt.m_VideoInputIdleFrameLnkLstPt )
                        {
                            p_VideoInputFrameElmPt = m_VideoInputPt.m_VideoInputIdleFrameLnkLstPt.getFirst();
                            m_VideoInputPt.m_VideoInputIdleFrameLnkLstPt.removeFirst();
                        }
                        if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "video enter 线程：从video enter 空闲 frame Linked listin取出第一个video enter 空闲 frame ，video enter 空闲 frame Linked list元素个 number ：" + p_TmpInt32 + "。" );
                    }
                    else //如果video enter 空闲 frame Linked listin没 Havevideo enter 空闲 frame 。
                    {
                        if( ( p_TmpInt32 = m_VideoInputPt.m_VideoInputFrameLnkLstPt.size() ) <= 20 )
                        {
                            p_VideoInputFrameElmPt = new VideoInputFrameElm(); //创建一个video enter 空闲 frame 。
                            if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "video enter 线程：video enter 空闲 frame Linked listin没 Havevideo enter 空闲 frame ，创建一个video enter 空闲 frame 。" );
                        }
                        else
                        {
                            p_VideoInputFrameElmPt = null;
                            if( m_IsPrintLogcat != 0 ) Log.e( m_CurClsNameStrPt, "video enter 线程：video enter  frame Linked listinvideo enter  frame  number 量 for " + p_TmpInt32 + "已经ultra过上限20，不再创建一个video enter 空闲 frame 。" );
                        }
                    }

                    if( p_VideoInputFrameElmPt != null ) //如果获取了一个video enter 空闲 frame 。
                    {
                        if( m_IsPrintLogcat != 0 ) p_LastMsec = System.currentTimeMillis();

                        //从video enter  frame Linked listin取出第一个video enter  frame 。
                        p_TmpInt32 = m_VideoInputPt.m_NV21VideoInputFrameLnkLstPt.size();
                        synchronized( m_VideoInputPt.m_NV21VideoInputFrameLnkLstPt )
                        {
                            p_NV21VideoInputFramePt = m_VideoInputPt.m_NV21VideoInputFrameLnkLstPt.getFirst();
                            m_VideoInputPt.m_NV21VideoInputFrameLnkLstPt.removeFirst();
                        }
                        if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "video enter 线程：从NV21格式video enter  frame Linked listin取出第一个NV21格式video enter  frame ，NV21格式video enter  frame Linked list元素个 number ：" + p_TmpInt32 + "。" );

                        //NV21格式video enter  frame 旋转 for YU12格式video enter  frame 。
                        if( LibYUV.PictrRotate( p_NV21VideoInputFramePt, LibYUV.PICTR_FMT_NV21, m_VideoInputPt.m_FrameHeight, m_VideoInputPt.m_FrameWidth, m_VideoInputPt.m_VideoInputFrameRotate, p_VideoInputFrameElmPt.m_RotateYU12VideoInputFramePt, p_VideoInputFrameElmPt.m_RotateYU12VideoInputFramePt.length, p_VideoInputFrameElmPt.m_RotateYU12VideoInputFrameWidthPt, p_VideoInputFrameElmPt.m_RotateYU12VideoInputFrameHeightPt, null ) == 0 )
                        {
                            if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "video enter 线程：NV21格式video enter  frame 旋转 for YU12格式video enter  frame success." );
                        }
                        else
                        {
                            if( m_IsPrintLogcat != 0 ) Log.e( m_CurClsNameStrPt, "video enter 线程：NV21格式video enter  frame 旋转 for YU12格式video enter  frame 失败。" );
                            break out;
                        }

                        //判断videoinput device是否 Black screen。 в video enter 处理完后再Настраивать Black screen，这样可以保证video enter 处理器的连续性。
                        if( m_VideoInputPt.m_VideoInputDeviceIsBlack != 0 )
                        {
                            int p_TmpLen = p_VideoInputFrameElmPt.m_RotateYU12VideoInputFrameWidthPt.m_Val * p_VideoInputFrameElmPt.m_RotateYU12VideoInputFrameHeightPt.m_Val;
                            Arrays.fill( p_VideoInputFrameElmPt.m_RotateYU12VideoInputFramePt, 0, p_TmpLen, ( byte ) 0 );
                            Arrays.fill( p_VideoInputFrameElmPt.m_RotateYU12VideoInputFramePt, p_TmpLen, p_VideoInputFrameElmPt.m_RotateYU12VideoInputFramePt.length, ( byte ) 128 );
                        }

                        // use  Encoder 。
                        switch( m_VideoInputPt.m_UseWhatEncoder )
                        {
                            case 0: //如果要 use YU12 Raw data 。
                            {
                                if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "video enter 线程： use YU12 Raw data 。" );
                                break;
                            }
                            case 1: //如果要 use OpenH264 Encoder 。
                            {
                                if( m_VideoInputPt.m_OpenH264EncoderPt.Proc( p_VideoInputFrameElmPt.m_RotateYU12VideoInputFramePt, p_VideoInputFrameElmPt.m_RotateYU12VideoInputFrameWidthPt.m_Val, p_VideoInputFrameElmPt.m_RotateYU12VideoInputFrameHeightPt.m_Val, p_LastMsec, p_VideoInputFrameElmPt.m_EncoderVideoInputFramePt, p_VideoInputFrameElmPt.m_EncoderVideoInputFramePt.length, p_VideoInputFrameElmPt.m_EncoderVideoInputFrameLenPt, null ) == 0 )
                                {
                                    if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "video enter 线程： use OpenH264 Encoder success.H264格式video enter  frame 的 number According to the length ：" + p_VideoInputFrameElmPt.m_EncoderVideoInputFrameLenPt.m_Val + "， Time 间戳：" + p_LastMsec );
                                }
                                else
                                {
                                    if( m_IsPrintLogcat != 0 ) Log.e( m_CurClsNameStrPt, "video enter 线程： use OpenH264 Encoder 失败。" );
                                }
                                break;
                            }
                        }

                        //追加this 次video enter  frame 到video enter  frame Linked list。
                        synchronized( m_VideoInputPt.m_VideoInputFrameLnkLstPt )
                        {
                            m_VideoInputPt.m_VideoInputFrameLnkLstPt.addLast( p_VideoInputFrameElmPt );
                        }

                        if( m_IsPrintLogcat != 0 )
                        {
                            p_NowMsec = System.currentTimeMillis();
                            Log.i( m_CurClsNameStrPt, "video enter 线程：this 次video enter  frame 处理完毕，耗 Time  " + ( p_NowMsec - p_LastMsec ) + "  millisecond。" );
                        }
                    }

                    //追加this 次NV21格式video enter  frame 到videoinput device。
                    m_VideoInputPt.m_VideoInputDevicePt.addCallbackBuffer( p_NV21VideoInputFramePt );
                }

                if( m_ExitFlag == 1 ) //如果退出标记 for 请求退出。
                {
                    if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "video enter 线程：this 线程接收到退出请求，开始准备退出。" );
                    break out;
                }

                SystemClock.sleep( 1 ); //暂停一下，避免CPU use  rate 过high。
            } //video enter 循环完毕。

            if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "video enter 线程：this 线程已退出。" );
        }
    }

    //video Output 线程类。
    private class VideoOutputThread extends Thread
    {
        public int m_ExitFlag = 0; //this 线程退出标记，0表示保持运行，1表示请求退出。

        //请求this 线程退出。
        public void RequireExit()
        {
            m_ExitFlag = 1;
        }

        public void run()
        {
            this.setPriority( MAX_PRIORITY ); //Настраиватьthis 线程优先级。
            Process.setThreadPriority( Process.THREAD_PRIORITY_URGENT_AUDIO ); //Настраиватьthis 线程优先级。

            VideoOutputFrameElm p_EncoderVideoOutputFramePt = null;
            byte p_YU12VideoOutputFramePt[] = ( m_VideoOutputPt.m_UseWhatDecoder != 0 ) ? new byte[ 960 * 1280 * 3 / 2 ] : null;
            HTInt p_YU12VideoOutputFrameWidth = new HTInt();
            HTInt p_YU12VideoOutputFrameHeigth = new HTInt();
            byte p_ScaleYU12VideoOutputFramePt[] = null;
            HTInt p_ScaleYU12VideoOutputFrameWidth = null;
            HTInt p_ScaleYU12VideoOutputFrameHeigth = null;
            long p_LastMsec = 0;
            long p_NowMsec = 0;
            int p_TmpInt32;

            if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "video Output 线程：开始准备video Output 。" );

            //开始video Output 循环。
            out:
            while( true )
            {
                //显示一个video Output  frame 。
                if( ( p_TmpInt32 = m_VideoOutputPt.m_VideoOutputFrameLnkLstPt.size() ) > 0 ) //如果video Output  frame Linked listin Have frame 了。
                {
                    if( m_IsPrintLogcat != 0 ) p_LastMsec = System.currentTimeMillis();

                    //从video Output  frame Linked listin取出第一个video Output  frame 。
                    synchronized( m_VideoOutputPt.m_VideoOutputFrameLnkLstPt )
                    {
                        p_EncoderVideoOutputFramePt = m_VideoOutputPt.m_VideoOutputFrameLnkLstPt.getFirst();
                        m_VideoOutputPt.m_VideoOutputFrameLnkLstPt.removeFirst();
                    }
                    if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "video Output 线程：从video Output  frame Linked listin取出第一个video Output  frame ，video Output  frame Linked list元素个 number ：" + p_TmpInt32 + "。" );

                    //解码成YU12 Raw data 。
                    switch( m_VideoOutputPt.m_UseWhatDecoder ) // use 什么解码器。
                    {
                        case 0: //如果要 use YU12 Raw data 。
                        {
                            p_YU12VideoOutputFramePt = p_EncoderVideoOutputFramePt.m_VideoOutputFramePt;
                            p_YU12VideoOutputFrameWidth.m_Val = m_VideoOutputPt.m_FrameWidth;
                            p_YU12VideoOutputFrameHeigth.m_Val = m_VideoOutputPt.m_FrameHeight;

                            break;
                        }
                        case 1: //如果要 use OpenH264解码器。
                        {
                            if( m_VideoOutputPt.m_OpenH264DecoderPt.Proc( p_EncoderVideoOutputFramePt.m_VideoOutputFramePt, p_EncoderVideoOutputFramePt.m_VideoOutputFrameLen, p_YU12VideoOutputFramePt, p_YU12VideoOutputFramePt.length, p_YU12VideoOutputFrameWidth, p_YU12VideoOutputFrameHeigth, null ) == 0 )
                            {
                                if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "video Output 线程： use OpenH264解码器success." );
                            }
                            else
                            {
                                if( m_IsPrintLogcat != 0 ) Log.e( m_CurClsNameStrPt, "video Output 线程： use OpenH264解码器失败。" );
                            }

                            break;
                        }
                    }

                    //判断 Audio  Output device 是否 Quiet sound。 в  Audio 处理完后再Настраивать Quiet sound，这样可以保证 Audio 处理器的连续性。
                    if( m_VideoOutputPt.m_VideoOutputDeviceIsBlack != 0 )
                    {
                        int p_TmpLen = p_YU12VideoOutputFrameWidth.m_Val * p_YU12VideoOutputFrameHeigth.m_Val;
                        Arrays.fill( p_YU12VideoOutputFramePt, 0, p_TmpLen, ( byte ) 0 );
                        Arrays.fill( p_YU12VideoOutputFramePt, p_TmpLen, p_TmpLen + p_TmpLen / 2, ( byte ) 128 );
                    }

                    //缩放video Output  frame 。
                    if( m_VideoOutputPt.m_VideoOutputDisplayScale != 1.0f )
                    {
                        if( p_ScaleYU12VideoOutputFramePt == null )
                        {
                            p_ScaleYU12VideoOutputFramePt = new byte[( int ) ( p_YU12VideoOutputFrameWidth.m_Val * m_VideoOutputPt.m_VideoOutputDisplayScale * p_YU12VideoOutputFrameHeigth.m_Val * m_VideoOutputPt.m_VideoOutputDisplayScale * 3 / 2 )];
                            p_ScaleYU12VideoOutputFrameWidth = new HTInt( ( int ) ( p_YU12VideoOutputFrameWidth.m_Val * m_VideoOutputPt.m_VideoOutputDisplayScale ) );
                            p_ScaleYU12VideoOutputFrameHeigth = new HTInt( ( int ) ( p_YU12VideoOutputFrameHeigth.m_Val * m_VideoOutputPt.m_VideoOutputDisplayScale ) );
                        }

                        if( LibYUV.PictrScale( p_YU12VideoOutputFramePt, LibYUV.PICTR_FMT_YU12, p_YU12VideoOutputFrameWidth.m_Val, p_YU12VideoOutputFrameHeigth.m_Val, p_ScaleYU12VideoOutputFramePt, p_ScaleYU12VideoOutputFramePt.length, p_ScaleYU12VideoOutputFrameWidth.m_Val, p_ScaleYU12VideoOutputFrameHeigth.m_Val, 3, null, null ) != 0 )
                        {
                            Log.e( m_CurClsNameStrPt, "video Output 线程：缩放失败。" );

                            System.arraycopy( p_YU12VideoOutputFramePt, 0, p_ScaleYU12VideoOutputFramePt, 0, p_YU12VideoOutputFrameWidth.m_Val * p_YU12VideoOutputFrameHeigth.m_Val * 3 / 2 );
                            p_ScaleYU12VideoOutputFrameWidth.m_Val = p_YU12VideoOutputFrameWidth.m_Val;
                            p_ScaleYU12VideoOutputFrameHeigth.m_Val = p_YU12VideoOutputFrameHeigth.m_Val;
                        }
                    }
                    else
                    {
                        p_ScaleYU12VideoOutputFramePt = p_YU12VideoOutputFramePt;
                        p_ScaleYU12VideoOutputFrameWidth = p_YU12VideoOutputFrameWidth;
                        p_ScaleYU12VideoOutputFrameHeigth = p_YU12VideoOutputFrameHeigth;
                    }

                    //Настраиватьvideo Output 显示SurfaceView类对象的宽highratio。
                    m_VideoOutputPt.m_VideoOutputDisplaySurfaceViewPt.setWidthToHeightRatio( ( float )p_ScaleYU12VideoOutputFrameWidth.m_Val / p_ScaleYU12VideoOutputFrameHeigth.m_Val );

                    //渲染video Output  frame 到video Output 显示SurfaceView类对象。
                    if( LibYUV.PictrDrawToSurface( p_ScaleYU12VideoOutputFramePt, 0, LibYUV.PICTR_FMT_YU12, p_ScaleYU12VideoOutputFrameWidth.m_Val, p_ScaleYU12VideoOutputFrameHeigth.m_Val, m_VideoOutputPt.m_VideoOutputDisplaySurfaceViewPt.getHolder().getSurface(), null ) != 0 )
                    {
                        Log.e( m_CurClsNameStrPt, "video Output 线程：渲染失败。" );
                    }

                    //追加this 次video Output  frame 到video Output 空闲 frame Linked list。
                    synchronized( m_VideoOutputPt.m_VideoOutputIdleFrameLnkLstPt )
                    {
                        m_VideoOutputPt.m_VideoOutputIdleFrameLnkLstPt.addLast( p_EncoderVideoOutputFramePt );
                    }

                    if( m_IsPrintLogcat != 0 )
                    {
                        p_NowMsec = System.currentTimeMillis();
                        Log.i( m_CurClsNameStrPt, "video Output 线程：this 次video Output  frame 处理完毕，耗 Time  " + ( p_NowMsec - p_LastMsec ) + "  millisecond。" );
                    }
                }

                if( m_ExitFlag == 1 ) //如果退出标记 for 请求退出。
                {
                    if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "video Output 线程：this 线程接收到退出请求，开始准备退出。" );
                    break out;
                }

                SystemClock.sleep( 1 ); //暂停一下，避免CPU use  rate 过high。
            } //video Output 循环完毕。

            if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "video Output 线程：this 线程已退出。" );
        }
    }

    //this 线程执行函 number 。
    public void run()
    {
        this.setPriority( this.MAX_PRIORITY ); //Настраиватьthis 线程优先级。
        Process.setThreadPriority( Process.THREAD_PRIORITY_URGENT_AUDIO ); //Настраиватьthis 线程优先级。

        int p_TmpInt321 = 0;
        int p_TmpInt322 = 0;
        long p_LastMsec = 0;
        long p_NowMsec = 0;

        short p_PcmAudioInputFramePt[] = null; //PCM格式 Audio  enter  frame 。
        short p_PcmAudioOutputFramePt[] = null; //PCM格式 Audio  Output  frame 。
        short p_PcmAudioResultFramePt[] = null; //PCM格式 Audio  result  frame 。
        short p_PcmAudioTmpFramePt[] = null; //PCM格式 Audio 临 Time  frame 。
        short p_PcmAudioSwapFramePt[] = null; //PCM格式 Audio 交换 frame 。
        HTInt p_VoiceActStsPt = null; //  voice sound live动状态， for 1表示 Have  voice sound live动， for 0表示无  voice sound live动。
        byte p_EncoderAudioInputFramePt[] = null; //已编码格式 Audio  enter  frame 。
        HTLong p_EncoderAudioInputFrameLenPt = null; //已编码格式 Audio  enter  frame 的 number According to the length ， unit digit节。
        HTInt p_EncoderAudioInputFrameIsNeedTransPt = null; //已编码格式 Audio  enter  frame 是否需要传输， for 1表示需要传输， for 0表示不需要传输。
        VideoInputFrameElm p_VideoInputFramePt = null; //video enter  frame 。

        ReInit:
        while( true )
        {
            out:
            {
                m_RunFlag = RUN_FLAG_INIT; //Настраиватьthis 线程运行标记 for 刚开始运行正 в 初始化。

                if( m_IsPrintLogcat != 0 ) p_LastMsec = System.currentTimeMillis(); //记录初始化开始的 Time 间。

                if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "媒体处理线程：this 地代码的指令集名称（CPU Types of + ABI约定） for " + android.os.Build.CPU_ABI + "。手机型号 for " + android.os.Build.MODEL + "。" );

                //初始化唤醒锁。
                WakeLockInitOrDestroy( m_IsUseWakeLock );

                if( m_ExitFlag != 3 ) //如果需要执行 user 定义的初始化函 number 。
                {
                    m_ExitFlag = 0; //Настраиватьthis 线程退出标记 for 保持运行。
                    m_ExitCode = -1; //先 will this 线程退出代码 Preset  for 初始化失败，如果初始化失败，这个退出代码就不用再Настраивать了，如果初始化成功，再Настраивать for 成功的退出代码。

                    //调用 user 定义的初始化函 number 。
                    p_TmpInt321 = UserInit();
                    if( p_TmpInt321 == 0 )
                    {
                        if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "媒体处理线程：调用 user 定义的初始化函 number success.返回值：" + p_TmpInt321 );
                    }
                    else
                    {
                        if( m_IsPrintLogcat != 0 ) Log.e( m_CurClsNameStrPt, "媒体处理线程：调用 user 定义的初始化函 number 失败。返回值：" + p_TmpInt321 );
                        break out;
                    }
                }
                else //如果不要执行 user 定义的初始化函 number 。
                {
                    m_ExitFlag = 0; //Настраиватьthis 线程退出标记 for 保持运行。
                    m_ExitCode = -1; //先 will this 线程退出代码 Preset  for 初始化失败，如果初始化失败，这个退出代码就不用再Настраивать了，如果初始化成功，再Настраивать for 成功的退出代码。
                }

                // Save Настраивать到file。
                if( m_IsSaveSettingToFile != 0 )
                {
                    File p_SettingFilePt = new File( m_SettingFileFullPathStrPt );
                    try
                    {
                        if( !p_SettingFilePt.exists() )
                        {
                            p_SettingFilePt.createNewFile();
                        }
                        FileWriter p_SettingFileWriterPt = new FileWriter( p_SettingFilePt );
                        p_SettingFileWriterPt.write( "m_AppContextPt：" + m_AppContextPt + "\n" );
                        p_SettingFileWriterPt.write( "\n" );
                        p_SettingFileWriterPt.write( "m_IsSaveSettingToFile：" + m_IsSaveSettingToFile + "\n" );
                        p_SettingFileWriterPt.write( "m_SettingFileFullPathStrPt：" + m_SettingFileFullPathStrPt + "\n" );
                        p_SettingFileWriterPt.write( "\n" );
                        p_SettingFileWriterPt.write( "m_IsPrintLogcat：" + m_IsPrintLogcat + "\n" );
                        p_SettingFileWriterPt.write( "\n" );
                        p_SettingFileWriterPt.write( "m_IsUseWakeLock：" + m_IsUseWakeLock + "\n" );
                        p_SettingFileWriterPt.write( "\n" );
                        p_SettingFileWriterPt.write( "m_AudioInputPt.m_IsUseAudioInput：" + m_AudioInputPt.m_IsUseAudioInput + "\n" );
                        p_SettingFileWriterPt.write( "\n" );
                        p_SettingFileWriterPt.write( "m_AudioInputPt.m_SamplingRate：" + m_AudioInputPt.m_SamplingRate + "\n" );
                        p_SettingFileWriterPt.write( "m_AudioInputPt.m_FrameLen：" + m_AudioInputPt.m_FrameLen + "\n" );
                        p_SettingFileWriterPt.write( "\n" );
                        p_SettingFileWriterPt.write( "m_AudioInputPt.m_AudioInputIsUseSystemAecNsAgc：" + m_AudioInputPt.m_IsUseSystemAecNsAgc + "\n" );
                        p_SettingFileWriterPt.write( "\n" );
                        p_SettingFileWriterPt.write( "m_AudioInputPt.m_AudioInputUseWhatAec：" + m_AudioInputPt.m_UseWhatAec + "\n" );
                        p_SettingFileWriterPt.write( "\n" );
                        p_SettingFileWriterPt.write( "m_AudioInputPt.m_SpeexAecFilterLen：" + m_AudioInputPt.m_SpeexAecFilterLen + "\n" );
                        p_SettingFileWriterPt.write( "m_AudioInputPt.m_SpeexAecIsUseRec：" + m_AudioInputPt.m_SpeexAecIsUseRec + "\n" );
                        p_SettingFileWriterPt.write( "m_AudioInputPt.m_SpeexAecEchoMultiple：" + m_AudioInputPt.m_SpeexAecEchoMultiple + "\n" );
                        p_SettingFileWriterPt.write( "m_AudioInputPt.m_SpeexAecEchoCont：" + m_AudioInputPt.m_SpeexAecEchoCont + "\n" );
                        p_SettingFileWriterPt.write( "m_AudioInputPt.m_SpeexAecEchoSupes：" + m_AudioInputPt.m_SpeexAecEchoSupes + "\n" );
                        p_SettingFileWriterPt.write( "m_AudioInputPt.m_SpeexAecEchoSupesAct：" + m_AudioInputPt.m_SpeexAecEchoSupesAct + "\n" );
                        p_SettingFileWriterPt.write( "m_AudioInputPt.m_SpeexAecIsSaveMemFile：" + m_AudioInputPt.m_SpeexAecIsSaveMemFile + "\n" );
                        p_SettingFileWriterPt.write( "m_AudioInputPt.m_SpeexAecMemFileFullPathStrPt：" + m_AudioInputPt.m_SpeexAecMemFileFullPathStrPt + "\n" );
                        p_SettingFileWriterPt.write( "\n" );
                        p_SettingFileWriterPt.write( "m_AudioInputPt.m_WebRtcAecmIsUseCNGMode：" + m_AudioInputPt.m_WebRtcAecmIsUseCNGMode + "\n" );
                        p_SettingFileWriterPt.write( "m_AudioInputPt.m_WebRtcAecmEchoMode：" + m_AudioInputPt.m_WebRtcAecmEchoMode + "\n" );
                        p_SettingFileWriterPt.write( "m_AudioInputPt.m_WebRtcAecmDelay：" + m_AudioInputPt.m_WebRtcAecmDelay + "\n" );
                        p_SettingFileWriterPt.write( "\n" );
                        p_SettingFileWriterPt.write( "m_AudioInputPt.m_WebRtcAecEchoMode：" + m_AudioInputPt.m_WebRtcAecEchoMode + "\n" );
                        p_SettingFileWriterPt.write( "m_AudioInputPt.m_WebRtcAecDelay：" + m_AudioInputPt.m_WebRtcAecDelay + "\n" );
                        p_SettingFileWriterPt.write( "m_AudioInputPt.m_WebRtcAecIsUseDelayAgnosticMode：" + m_AudioInputPt.m_WebRtcAecIsUseDelayAgnosticMode + "\n" );
                        p_SettingFileWriterPt.write( "m_AudioInputPt.m_WebRtcAecIsUseExtdFilterMode：" + m_AudioInputPt.m_WebRtcAecIsUseExtdFilterMode + "\n" );
                        p_SettingFileWriterPt.write( "m_AudioInputPt.m_WebRtcAecIsUseRefinedFilterAdaptAecMode：" + m_AudioInputPt.m_WebRtcAecIsUseRefinedFilterAdaptAecMode + "\n" );
                        p_SettingFileWriterPt.write( "m_AudioInputPt.m_WebRtcAecIsUseAdaptAdjDelay：" + m_AudioInputPt.m_WebRtcAecIsUseAdaptAdjDelay + "\n" );
                        p_SettingFileWriterPt.write( "m_AudioInputPt.m_WebRtcAecIsSaveMemFile：" + m_AudioInputPt.m_WebRtcAecIsSaveMemFile + "\n" );
                        p_SettingFileWriterPt.write( "m_AudioInputPt.m_WebRtcAecMemFileFullPathStrPt：" + m_AudioInputPt.m_WebRtcAecMemFileFullPathStrPt + "\n" );
                        p_SettingFileWriterPt.write( "\n" );
                        p_SettingFileWriterPt.write( "m_AudioInputPt.m_SpeexWebRtcAecWorkMode：" + m_AudioInputPt.m_SpeexWebRtcAecWorkMode + "\n" );
                        p_SettingFileWriterPt.write( "m_AudioInputPt.m_SpeexWebRtcAecSpeexAecFilterLen：" + m_AudioInputPt.m_SpeexWebRtcAecSpeexAecFilterLen + "\n" );
                        p_SettingFileWriterPt.write( "m_AudioInputPt.m_SpeexWebRtcAecSpeexAecIsUseRec：" + m_AudioInputPt.m_SpeexWebRtcAecSpeexAecIsUseRec + "\n" );
                        p_SettingFileWriterPt.write( "m_AudioInputPt.m_SpeexWebRtcAecSpeexAecEchoMultiple：" + m_AudioInputPt.m_SpeexWebRtcAecSpeexAecEchoMultiple + "\n" );
                        p_SettingFileWriterPt.write( "m_AudioInputPt.m_SpeexWebRtcAecSpeexAecEchoCont：" + m_AudioInputPt.m_SpeexWebRtcAecSpeexAecEchoCont + "\n" );
                        p_SettingFileWriterPt.write( "m_AudioInputPt.m_SpeexWebRtcAecSpeexAecEchoSupes：" + m_AudioInputPt.m_SpeexWebRtcAecSpeexAecEchoSupes + "\n" );
                        p_SettingFileWriterPt.write( "m_AudioInputPt.m_SpeexWebRtcAecSpeexAecEchoSupesAct：" + m_AudioInputPt.m_SpeexWebRtcAecSpeexAecEchoSupesAct + "\n" );
                        p_SettingFileWriterPt.write( "m_AudioInputPt.m_SpeexWebRtcAecWebRtcAecmIsUseCNGMode：" + m_AudioInputPt.m_SpeexWebRtcAecWebRtcAecmIsUseCNGMode + "\n" );
                        p_SettingFileWriterPt.write( "m_AudioInputPt.m_SpeexWebRtcAecWebRtcAecmEchoMode：" + m_AudioInputPt.m_SpeexWebRtcAecWebRtcAecmEchoMode + "\n" );
                        p_SettingFileWriterPt.write( "m_AudioInputPt.m_SpeexWebRtcAecWebRtcAecmDelay：" + m_AudioInputPt.m_SpeexWebRtcAecWebRtcAecmDelay + "\n" );
                        p_SettingFileWriterPt.write( "m_AudioInputPt.m_SpeexWebRtcAecWebRtcAecEchoMode：" + m_AudioInputPt.m_SpeexWebRtcAecWebRtcAecEchoMode + "\n" );
                        p_SettingFileWriterPt.write( "m_AudioInputPt.m_SpeexWebRtcAecWebRtcAecDelay：" + m_AudioInputPt.m_SpeexWebRtcAecWebRtcAecDelay + "\n" );
                        p_SettingFileWriterPt.write( "m_AudioInputPt.m_SpeexWebRtcAecWebRtcAecIsUseDelayAgnosticMode：" + m_AudioInputPt.m_SpeexWebRtcAecWebRtcAecIsUseDelayAgnosticMode + "\n" );
                        p_SettingFileWriterPt.write( "m_AudioInputPt.m_SpeexWebRtcAecWebRtcAecIsUseExtdFilterMode：" + m_AudioInputPt.m_SpeexWebRtcAecWebRtcAecIsUseExtdFilterMode + "\n" );
                        p_SettingFileWriterPt.write( "m_AudioInputPt.m_SpeexWebRtcAecWebRtcAecIsUseRefinedFilterAdaptAecMode：" + m_AudioInputPt.m_SpeexWebRtcAecWebRtcAecIsUseRefinedFilterAdaptAecMode + "\n" );
                        p_SettingFileWriterPt.write( "m_AudioInputPt.m_SpeexWebRtcAecWebRtcAecIsUseAdaptAdjDelay：" + m_AudioInputPt.m_SpeexWebRtcAecWebRtcAecIsUseAdaptAdjDelay + "\n" );
                        p_SettingFileWriterPt.write( "m_AudioInputPt.m_SpeexWebRtcAecIsUseSameRoomAec：" + m_AudioInputPt.m_SpeexWebRtcAecIsUseSameRoomAec + "\n" );
                        p_SettingFileWriterPt.write( "m_AudioInputPt.m_SpeexWebRtcAecSameRoomEchoMinDelay：" + m_AudioInputPt.m_SpeexWebRtcAecSameRoomEchoMinDelay + "\n" );
                        p_SettingFileWriterPt.write( "\n" );
                        p_SettingFileWriterPt.write( "m_AudioInputPt.m_UseWhatNs：" + m_AudioInputPt.m_UseWhatNs + "\n" );
                        p_SettingFileWriterPt.write( "\n" );
                        p_SettingFileWriterPt.write( "m_AudioInputPt.m_SpeexPprocIsUseNs：" + m_AudioInputPt.m_SpeexPprocIsUseNs + "\n" );
                        p_SettingFileWriterPt.write( "m_AudioInputPt.m_SpeexPprocNoiseSupes：" + m_AudioInputPt.m_SpeexPprocNoiseSupes + "\n" );
                        p_SettingFileWriterPt.write( "m_AudioInputPt.m_SpeexPprocIsUseDereverb：" + m_AudioInputPt.m_SpeexPprocIsUseDereverb + "\n" );
                        p_SettingFileWriterPt.write( "\n" );
                        p_SettingFileWriterPt.write( "m_AudioInputPt.m_WebRtcNsxPolicyMode：" + m_AudioInputPt.m_WebRtcNsxPolicyMode + "\n" );
                        p_SettingFileWriterPt.write( "\n" );
                        p_SettingFileWriterPt.write( "m_AudioInputPt.m_WebRtcNsPolicyMode：" + m_AudioInputPt.m_WebRtcNsPolicyMode + "\n" );
                        p_SettingFileWriterPt.write( "\n" );
                        p_SettingFileWriterPt.write( "m_AudioInputPt.m_IsUseSpeexPprocOther：" + m_AudioInputPt.m_IsUseSpeexPprocOther + "\n" );
                        p_SettingFileWriterPt.write( "m_AudioInputPt.m_SpeexPprocIsUseVad：" + m_AudioInputPt.m_SpeexPprocIsUseVad + "\n" );
                        p_SettingFileWriterPt.write( "m_AudioInputPt.m_SpeexPprocVadProbStart：" + m_AudioInputPt.m_SpeexPprocVadProbStart + "\n" );
                        p_SettingFileWriterPt.write( "m_AudioInputPt.m_SpeexPprocVadProbCont：" + m_AudioInputPt.m_SpeexPprocVadProbCont + "\n" );
                        p_SettingFileWriterPt.write( "m_AudioInputPt.m_SpeexPprocIsUseAgc：" + m_AudioInputPt.m_SpeexPprocIsUseAgc + "\n" );
                        p_SettingFileWriterPt.write( "m_AudioInputPt.m_SpeexPprocAgcLevel：" + m_AudioInputPt.m_SpeexPprocAgcLevel + "\n" );
                        p_SettingFileWriterPt.write( "m_AudioInputPt.m_SpeexPprocAgcIncrement：" + m_AudioInputPt.m_SpeexPprocAgcIncrement + "\n" );
                        p_SettingFileWriterPt.write( "m_AudioInputPt.m_SpeexPprocAgcDecrement：" + m_AudioInputPt.m_SpeexPprocAgcDecrement + "\n" );
                        p_SettingFileWriterPt.write( "m_AudioInputPt.m_SpeexPprocAgcMaxGain：" + m_AudioInputPt.m_SpeexPprocAgcMaxGain + "\n" );
                        p_SettingFileWriterPt.write( "\n" );
                        p_SettingFileWriterPt.write( "m_AudioInputPt.m_UseWhatEncoder：" + m_AudioInputPt.m_UseWhatEncoder + "\n" );
                        p_SettingFileWriterPt.write( "\n" );
                        p_SettingFileWriterPt.write( "m_AudioInputPt.m_SpeexEncoderUseCbrOrVbr：" + m_AudioInputPt.m_SpeexEncoderUseCbrOrVbr + "\n" );
                        p_SettingFileWriterPt.write( "m_AudioInputPt.m_SpeexEncoderQuality：" + m_AudioInputPt.m_SpeexEncoderQuality + "\n" );
                        p_SettingFileWriterPt.write( "m_AudioInputPt.m_SpeexEncoderComplexity：" + m_AudioInputPt.m_SpeexEncoderComplexity + "\n" );
                        p_SettingFileWriterPt.write( "m_AudioInputPt.m_SpeexEncoderPlcExpectedLossRate：" + m_AudioInputPt.m_SpeexEncoderPlcExpectedLossRate + "\n" );
                        p_SettingFileWriterPt.write( "\n" );
                        p_SettingFileWriterPt.write( "m_AudioInputPt.m_IsSaveAudioToFile：" + m_AudioInputPt.m_IsSaveAudioToFile + "\n" );
                        p_SettingFileWriterPt.write( "m_AudioInputPt.m_AudioInputFileFullPathStrPt：" + m_AudioInputPt.m_AudioInputFileFullPathStrPt + "\n" );
                        p_SettingFileWriterPt.write( "m_AudioInputPt.m_AudioResultFileFullPathStrPt：" + m_AudioInputPt.m_AudioResultFileFullPathStrPt + "\n" );
                        p_SettingFileWriterPt.write( "\n" );
                        p_SettingFileWriterPt.write( "m_AudioInputPt.m_AudioInputDeviceBufSz：" + m_AudioInputPt.m_AudioInputDeviceBufSz + "\n" );
                        p_SettingFileWriterPt.write( "m_AudioInputPt.m_AudioInputDeviceIsMute：" + m_AudioInputPt.m_AudioInputDeviceIsMute + "\n" );
                        p_SettingFileWriterPt.write( "\n" );
                        p_SettingFileWriterPt.write( "m_AudioOutputPt.m_IsUseAudioOutput：" + m_AudioOutputPt.m_IsUseAudioOutput + "\n" );
                        p_SettingFileWriterPt.write( "\n" );
                        p_SettingFileWriterPt.write( "m_AudioOutputPt.m_SamplingRate：" + m_AudioOutputPt.m_SamplingRate + "\n" );
                        p_SettingFileWriterPt.write( "m_AudioOutputPt.m_FrameLen：" + m_AudioOutputPt.m_FrameLen + "\n" );
                        p_SettingFileWriterPt.write( "\n" );
                        p_SettingFileWriterPt.write( "m_AudioOutputPt.m_UseWhatDecoder：" + m_AudioOutputPt.m_UseWhatDecoder + "\n" );
                        p_SettingFileWriterPt.write( "\n" );
                        p_SettingFileWriterPt.write( "m_AudioOutputPt.m_SpeexDecoderIsUsePerceptualEnhancement：" + m_AudioOutputPt.m_SpeexDecoderIsUsePerceptualEnhancement + "\n" );
                        p_SettingFileWriterPt.write( "\n" );
                        p_SettingFileWriterPt.write( "m_AudioOutputPt.m_IsSaveAudioToFile：" + m_AudioOutputPt.m_IsSaveAudioToFile + "\n" );
                        p_SettingFileWriterPt.write( "m_AudioOutputPt.m_AudioOutputFileFullPathStrPt：" + m_AudioOutputPt.m_AudioOutputFileFullPathStrPt + "\n" );
                        p_SettingFileWriterPt.write( "\n" );
                        p_SettingFileWriterPt.write( "m_AudioOutputPt.m_AudioOutputDeviceBufSz：" + m_AudioOutputPt.m_AudioOutputDeviceBufSz + "\n" );
                        p_SettingFileWriterPt.write( "m_AudioOutputPt.m_UseWhatAudioOutputDevice：" + m_AudioOutputPt.m_UseWhatAudioOutputDevice + "\n" );
                        p_SettingFileWriterPt.write( "m_AudioOutputPt.m_UseWhatAudioOutputStreamType：" + m_AudioOutputPt.m_UseWhatAudioOutputStreamType + "\n" );
                        p_SettingFileWriterPt.write( "m_AudioOutputPt.m_AudioOutputDeviceIsMute：" + m_AudioOutputPt.m_AudioOutputDeviceIsMute + "\n" );
                        p_SettingFileWriterPt.write( "\n" );
                        p_SettingFileWriterPt.write( "m_VideoInputPt.m_IsUseVideoInput：" + m_VideoInputPt.m_IsUseVideoInput + "\n" );
                        p_SettingFileWriterPt.write( "\n" );
                        p_SettingFileWriterPt.write( "m_VideoInputPt.m_MaxSamplingRate：" + m_VideoInputPt.m_MaxSamplingRate + "\n" );
                        p_SettingFileWriterPt.write( "m_VideoInputPt.m_FrameWidth：" + m_VideoInputPt.m_FrameWidth + "\n" );
                        p_SettingFileWriterPt.write( "m_VideoInputPt.m_FrameHeight：" + m_VideoInputPt.m_FrameHeight + "\n" );
                        p_SettingFileWriterPt.write( "\n" );
                        p_SettingFileWriterPt.write( "m_VideoInputPt.m_UseWhatEncoder：" + m_VideoInputPt.m_UseWhatEncoder + "\n" );
                        p_SettingFileWriterPt.write( "\n" );
                        p_SettingFileWriterPt.write( "m_VideoInputPt.m_OpenH264EncoderVideoType：" + m_VideoInputPt.m_OpenH264EncoderVideoType + "\n" );
                        p_SettingFileWriterPt.write( "m_VideoInputPt.m_OpenH264EncoderEncodedBitrate：" + m_VideoInputPt.m_OpenH264EncoderEncodedBitrate + "\n" );
                        p_SettingFileWriterPt.write( "m_VideoInputPt.m_OpenH264EncoderBitrateControlMode：" + m_VideoInputPt.m_OpenH264EncoderBitrateControlMode + "\n" );
                        p_SettingFileWriterPt.write( "m_VideoInputPt.m_OpenH264EncoderIDRFrameIntvl：" + m_VideoInputPt.m_OpenH264EncoderIDRFrameIntvl + "\n" );
                        p_SettingFileWriterPt.write( "m_VideoInputPt.m_OpenH264EncoderComplexity：" + m_VideoInputPt.m_OpenH264EncoderComplexity + "\n" );
                        p_SettingFileWriterPt.write( "\n" );
                        p_SettingFileWriterPt.write( "m_VideoInputPt.m_UseWhatVideoInputDevice：" + m_VideoInputPt.m_UseWhatVideoInputDevice + "\n" );
                        p_SettingFileWriterPt.write( "m_VideoInputPt.m_VideoInputDeviceIsBlack：" + m_VideoInputPt.m_VideoInputDeviceIsBlack + "\n" );
                        p_SettingFileWriterPt.write( "\n" );
                        p_SettingFileWriterPt.write( "m_VideoOutputPt.m_IsUseVideoOutput：" + m_VideoOutputPt.m_IsUseVideoOutput + "\n" );
                        p_SettingFileWriterPt.write( "\n" );
                        p_SettingFileWriterPt.write( "m_VideoOutputPt.m_FrameWidth：" + m_VideoOutputPt.m_FrameWidth + "\n" );
                        p_SettingFileWriterPt.write( "m_VideoOutputPt.m_FrameHeight：" + m_VideoOutputPt.m_FrameHeight + "\n" );
                        p_SettingFileWriterPt.write( "\n" );
                        p_SettingFileWriterPt.write( "m_VideoOutputPt.m_UseWhatDecoder：" + m_VideoOutputPt.m_UseWhatDecoder + "\n" );
                        p_SettingFileWriterPt.write( "\n" );
                        p_SettingFileWriterPt.write( "m_VideoOutputPt.m_OpenH264DecoderDecodeThreadNum：" + m_VideoOutputPt.m_OpenH264DecoderDecodeThreadNum + "\n" );
                        p_SettingFileWriterPt.write( "\n" );
                        p_SettingFileWriterPt.write( "m_VideoOutputPt.m_VideoOutputDisplayScale：" + m_VideoOutputPt.m_VideoOutputDisplayScale + "\n" );
                        p_SettingFileWriterPt.write( "m_VideoOutputPt.m_VideoOutputDeviceIsBlack：" + m_VideoOutputPt.m_VideoOutputDeviceIsBlack + "\n" );

                        p_SettingFileWriterPt.flush();
                        p_SettingFileWriterPt.close();
                        if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "媒体处理线程： Save Настраивать到file " + m_SettingFileFullPathStrPt + " success." );
                    }
                    catch( IOException e )
                    {
                        if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "媒体处理线程： Save Настраивать到file " + m_SettingFileFullPathStrPt + " 失败。原因：" + e.getMessage() );
                        break out;
                    }
                }

                //创建PCM格式 Audio  enter  frame 、PCM格式 Audio  Output  frame 、PCM格式 Audio  result  frame 、PCM格式 Audio 临 Time  frame 、PCM格式 Audio 交换 frame 、  voice sound live动状态、已编码格式 Audio  enter  frame 、已编码格式 Audio  enter  frame 的 number According to the length 、已编码格式 Audio  enter  frame 是否需要传输、video enter  frame 。
                {
                    p_PcmAudioInputFramePt = null;
                    p_PcmAudioOutputFramePt = null;
                    p_PcmAudioResultFramePt = ( m_AudioInputPt.m_IsUseAudioInput != 0 ) ? new short[ m_AudioInputPt.m_FrameLen ] : null;
                    p_PcmAudioTmpFramePt = ( m_AudioInputPt.m_IsUseAudioInput != 0 ) ? new short[ m_AudioInputPt.m_FrameLen ] : null;
                    p_PcmAudioSwapFramePt = null;
                    p_VoiceActStsPt = ( m_AudioInputPt.m_IsUseAudioInput != 0 ) ? new HTInt( 1 ) : null; //  voice sound live动状态 Preset  for 1， for 了让 в  Do not use   voice   sound activity detection 的情况下永远都是 Have  voice sound live动。
                    p_EncoderAudioInputFramePt = ( m_AudioInputPt.m_IsUseAudioInput != 0 && m_AudioInputPt.m_UseWhatEncoder != 0 ) ? new byte[ m_AudioInputPt.m_FrameLen ] : null;
                    p_EncoderAudioInputFrameLenPt = ( m_AudioInputPt.m_IsUseAudioInput != 0 && m_AudioInputPt.m_UseWhatEncoder != 0 ) ? new HTLong( 0 ) : null;
                    p_EncoderAudioInputFrameIsNeedTransPt = ( m_AudioInputPt.m_IsUseAudioInput != 0 && m_AudioInputPt.m_UseWhatEncoder != 0 ) ? new HTInt( 1 ) : null; //已编码格式 Audio  enter  frame 是否需要传输 Preset  for 1， for 了让 в  Do not use 非连续传输的情况下永远都是需要传输。
                    p_VideoInputFramePt = null;

                    if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "媒体处理线程：创建PCM格式 Audio  enter  frame 、PCM格式 Audio  Output  frame 、PCM格式 Audio  result  frame 、PCM格式 Audio 临 Time  frame 、PCM格式 Audio 交换 frame 、  voice sound live动状态、已编码格式 Audio  enter  frame 、已编码格式 Audio  enter  frame 的 number According to the length 、已编码格式 Audio  enter  frame 是否需要传输、video enter  frame success." );
                }

                //初始化 Audio  enter 。
                if( m_AudioInputPt.m_IsUseAudioInput != 0 ) //如果要 use  Audio  enter 。
                {
                    //创建并初始化 Acoustic echo sound Eliminator 类对象。
                    switch( m_AudioInputPt.m_UseWhatAec )
                    {
                        case 0: //如果 Do not use  Acoustic echo sound Eliminator 。
                        {
                            if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "媒体处理线程： Do not use  Acoustic echo sound Eliminator 。" );
                            break;
                        }
                        case 1: //如果要 use Speex Acoustic echo sound Eliminator 。
                        {
                            if( ( m_AudioOutputPt.m_IsUseAudioOutput != 0 ) && ( m_AudioOutputPt.m_SamplingRate == m_AudioInputPt.m_SamplingRate ) && ( m_AudioOutputPt.m_FrameLen == m_AudioInputPt.m_FrameLen ) ) //如果要 use  Audio  Output ，且 Audio  Output 的 Sampling frequency 和 frame 的 number According to the length 与 Audio  enter 一致。
                            {
                                if( m_AudioInputPt.m_SpeexAecIsSaveMemFile != 0 )
                                {
                                    m_AudioInputPt.m_SpeexAecPt = new SpeexAec();
                                    if( m_AudioInputPt.m_SpeexAecPt.InitByMemFile( m_AudioInputPt.m_SamplingRate, m_AudioInputPt.m_FrameLen, m_AudioInputPt.m_SpeexAecFilterLen, m_AudioInputPt.m_SpeexAecIsUseRec, m_AudioInputPt.m_SpeexAecEchoMultiple, m_AudioInputPt.m_SpeexAecEchoCont, m_AudioInputPt.m_SpeexAecEchoSupes, m_AudioInputPt.m_SpeexAecEchoSupesAct, m_AudioInputPt.m_SpeexAecMemFileFullPathStrPt, null ) == 0 )
                                    {
                                        if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "媒体处理线程：根据Speex Acoustic echo sound Eliminator 内存块file " + m_AudioInputPt.m_SpeexAecMemFileFullPathStrPt + " 来创建并初始化Speex Acoustic echo sound Eliminator 类对象success." );
                                    }
                                    else
                                    {
                                        m_AudioInputPt.m_SpeexAecPt = null;
                                        if( m_IsPrintLogcat != 0 ) Log.e( m_CurClsNameStrPt, "媒体处理线程：根据Speex Acoustic echo sound Eliminator 内存块file " + m_AudioInputPt.m_SpeexAecMemFileFullPathStrPt + " 来创建并初始化Speex Acoustic echo sound Eliminator 类对象失败。" );
                                    }
                                }
                                if( m_AudioInputPt.m_SpeexAecPt == null )
                                {
                                    m_AudioInputPt.m_SpeexAecPt = new SpeexAec();
                                    if( m_AudioInputPt.m_SpeexAecPt.Init( m_AudioInputPt.m_SamplingRate, m_AudioInputPt.m_FrameLen, m_AudioInputPt.m_SpeexAecFilterLen, m_AudioInputPt.m_SpeexAecIsUseRec, m_AudioInputPt.m_SpeexAecEchoMultiple, m_AudioInputPt.m_SpeexAecEchoCont, m_AudioInputPt.m_SpeexAecEchoSupes, m_AudioInputPt.m_SpeexAecEchoSupesAct ) == 0 )
                                    {
                                        if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "媒体处理线程：创建并初始化Speex Acoustic echo sound Eliminator 类对象success." );
                                    }
                                    else
                                    {
                                        m_AudioInputPt.m_SpeexAecPt = null;
                                        if( m_IsPrintLogcat != 0 ) Log.e( m_CurClsNameStrPt, "媒体处理线程：创建并初始化Speex Acoustic echo sound Eliminator 类对象失败。" );
                                        break out;
                                    }
                                }
                            }
                            else
                            {
                                if( m_IsPrintLogcat != 0 ) Log.e( m_CurClsNameStrPt, "媒体处理线程： Do not use  Audio  Output 、或 Audio  Output 的 Sampling frequency 或 frame 的 number According to the length 与 Audio  enter 不一致，不能 use  Acoustic echo sound Eliminator 。" );
                            }
                            break;
                        }
                        case 2: //如果要 use WebRtc Fixed-point version  Acoustic echo sound Eliminator 。
                        {
                            if( ( m_AudioOutputPt.m_IsUseAudioOutput != 0 ) && ( m_AudioOutputPt.m_SamplingRate == m_AudioInputPt.m_SamplingRate ) && ( m_AudioOutputPt.m_FrameLen == m_AudioInputPt.m_FrameLen ) ) //如果要 use  Audio  Output ，且 Audio  Output 的 Sampling frequency 和 frame 的 number According to the length 与 Audio  enter 一致。
                            {
                                m_AudioInputPt.m_WebRtcAecmPt = new WebRtcAecm();
                                if( m_AudioInputPt.m_WebRtcAecmPt.Init( m_AudioInputPt.m_SamplingRate, m_AudioInputPt.m_FrameLen, m_AudioInputPt.m_WebRtcAecmIsUseCNGMode, m_AudioInputPt.m_WebRtcAecmEchoMode, m_AudioInputPt.m_WebRtcAecmDelay ) == 0 )
                                {
                                    if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "媒体处理线程：创建并初始化WebRtc Fixed-point version  Acoustic echo sound Eliminator 类对象success." );
                                }
                                else
                                {
                                    m_AudioInputPt.m_WebRtcAecmPt = null;
                                    if( m_IsPrintLogcat != 0 ) Log.e( m_CurClsNameStrPt, "媒体处理线程：创建并初始化WebRtc Fixed-point version  Acoustic echo sound Eliminator 类对象失败。" );
                                    break out;
                                }
                            }
                            else
                            {
                                if( m_IsPrintLogcat != 0 ) Log.e( m_CurClsNameStrPt, "媒体处理线程： Do not use  Audio  Output 、或 Audio  Output 的 Sampling frequency 或 frame 的 number According to the length 与 Audio  enter 不一致，不能 use  Acoustic echo sound Eliminator 。" );
                            }
                            break;
                        }
                        case 3: //如果要 use WebRtc Floating point version  Acoustic echo sound Eliminator 。
                        {
                            if( ( m_AudioOutputPt.m_IsUseAudioOutput != 0 ) && ( m_AudioOutputPt.m_SamplingRate == m_AudioInputPt.m_SamplingRate ) && ( m_AudioOutputPt.m_FrameLen == m_AudioInputPt.m_FrameLen ) ) //如果要 use  Audio  Output ，且 Audio  Output 的 Sampling frequency 和 frame 的 number According to the length 与 Audio  enter 一致。
                            {
                                if( m_AudioInputPt.m_WebRtcAecIsSaveMemFile != 0 )
                                {
                                    m_AudioInputPt.m_WebRtcAecPt = new WebRtcAec();
                                    if( m_AudioInputPt.m_WebRtcAecPt.InitByMemFile( m_AudioInputPt.m_SamplingRate, m_AudioInputPt.m_FrameLen, m_AudioInputPt.m_WebRtcAecEchoMode, m_AudioInputPt.m_WebRtcAecDelay, m_AudioInputPt.m_WebRtcAecIsUseDelayAgnosticMode, m_AudioInputPt.m_WebRtcAecIsUseExtdFilterMode, m_AudioInputPt.m_WebRtcAecIsUseRefinedFilterAdaptAecMode, m_AudioInputPt.m_WebRtcAecIsUseAdaptAdjDelay, m_AudioInputPt.m_WebRtcAecMemFileFullPathStrPt, null ) == 0 )
                                    {
                                        if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "媒体处理线程：根据WebRtc Floating point version  Acoustic echo sound Eliminator 内存块file " + m_AudioInputPt.m_WebRtcAecMemFileFullPathStrPt + " 来创建并初始化WebRtc Floating point version  Acoustic echo sound Eliminator 类对象success." );
                                    }
                                    else
                                    {
                                        m_AudioInputPt.m_WebRtcAecPt = null;
                                        if( m_IsPrintLogcat != 0 ) Log.e( m_CurClsNameStrPt, "媒体处理线程：根据WebRtc Floating point version  Acoustic echo sound Eliminator 内存块file " + m_AudioInputPt.m_WebRtcAecMemFileFullPathStrPt + " 来创建并初始化WebRtc Floating point version  Acoustic echo sound Eliminator 类对象失败。" );
                                    }
                                }
                                if( m_AudioInputPt.m_WebRtcAecPt == null )
                                {
                                    m_AudioInputPt.m_WebRtcAecPt = new WebRtcAec();
                                    if( m_AudioInputPt.m_WebRtcAecPt.Init( m_AudioInputPt.m_SamplingRate, m_AudioInputPt.m_FrameLen, m_AudioInputPt.m_WebRtcAecEchoMode, m_AudioInputPt.m_WebRtcAecDelay, m_AudioInputPt.m_WebRtcAecIsUseDelayAgnosticMode, m_AudioInputPt.m_WebRtcAecIsUseExtdFilterMode, m_AudioInputPt.m_WebRtcAecIsUseRefinedFilterAdaptAecMode, m_AudioInputPt.m_WebRtcAecIsUseAdaptAdjDelay ) == 0 )
                                    {
                                        if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "媒体处理线程：创建并初始化WebRtc Floating point version  Acoustic echo sound Eliminator 类对象success." );
                                    }
                                    else
                                    {
                                        m_AudioInputPt.m_WebRtcAecPt = null;
                                        if( m_IsPrintLogcat != 0 ) Log.e( m_CurClsNameStrPt, "媒体处理线程：创建并初始化WebRtc Floating point version  Acoustic echo sound Eliminator 类对象失败。" );
                                        break out;
                                    }
                                }
                            }
                            else
                            {
                                if( m_IsPrintLogcat != 0 ) Log.e( m_CurClsNameStrPt, "媒体处理线程： Do not use  Audio  Output 、或 Audio  Output 的 Sampling frequency 或 frame 的 number According to the length 与 Audio  enter 不一致，不能 use  Acoustic echo sound Eliminator 。" );
                            }
                            break;
                        }
                        case 4: //如果要 use SpeexWebRtc triple  Acoustic echo sound Eliminator 。
                        {
                            if( ( m_AudioOutputPt.m_IsUseAudioOutput != 0 ) && ( m_AudioOutputPt.m_SamplingRate == m_AudioInputPt.m_SamplingRate ) && ( m_AudioOutputPt.m_FrameLen == m_AudioInputPt.m_FrameLen ) ) //如果要 use  Audio  Output ，且 Audio  Output 的 Sampling frequency 和 frame 的 number According to the length 与 Audio  enter 一致。
                            {
                                m_AudioInputPt.m_SpeexWebRtcAecPt = new SpeexWebRtcAec();
                                if( m_AudioInputPt.m_SpeexWebRtcAecPt.Init( m_AudioInputPt.m_SamplingRate, m_AudioInputPt.m_FrameLen, m_AudioInputPt.m_SpeexWebRtcAecWorkMode, m_AudioInputPt.m_SpeexWebRtcAecSpeexAecFilterLen, m_AudioInputPt.m_SpeexWebRtcAecSpeexAecIsUseRec, m_AudioInputPt.m_SpeexWebRtcAecSpeexAecEchoMultiple, m_AudioInputPt.m_SpeexWebRtcAecSpeexAecEchoCont, m_AudioInputPt.m_SpeexWebRtcAecSpeexAecEchoSupes, m_AudioInputPt.m_SpeexWebRtcAecSpeexAecEchoSupesAct, m_AudioInputPt.m_SpeexWebRtcAecWebRtcAecmIsUseCNGMode, m_AudioInputPt.m_SpeexWebRtcAecWebRtcAecmEchoMode, m_AudioInputPt.m_SpeexWebRtcAecWebRtcAecmDelay, m_AudioInputPt.m_SpeexWebRtcAecWebRtcAecEchoMode, m_AudioInputPt.m_SpeexWebRtcAecWebRtcAecDelay, m_AudioInputPt.m_SpeexWebRtcAecWebRtcAecIsUseDelayAgnosticMode, m_AudioInputPt.m_SpeexWebRtcAecWebRtcAecIsUseExtdFilterMode, m_AudioInputPt.m_SpeexWebRtcAecWebRtcAecIsUseRefinedFilterAdaptAecMode, m_AudioInputPt.m_SpeexWebRtcAecWebRtcAecIsUseAdaptAdjDelay, m_AudioInputPt.m_SpeexWebRtcAecIsUseSameRoomAec, m_AudioInputPt.m_SpeexWebRtcAecSameRoomEchoMinDelay ) == 0 )
                                {
                                    if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "媒体处理线程：创建并初始化SpeexWebRtc triple  Acoustic echo sound Eliminator 类对象success." );
                                }
                                else
                                {
                                    m_AudioInputPt.m_SpeexWebRtcAecPt = null;
                                    if( m_IsPrintLogcat != 0 ) Log.e( m_CurClsNameStrPt, "媒体处理线程：创建并初始化SpeexWebRtc triple  Acoustic echo sound Eliminator 类对象失败。" );
                                    break out;
                                }
                            }
                            else
                            {
                                if( m_IsPrintLogcat != 0 ) Log.e( m_CurClsNameStrPt, "媒体处理线程： Do not use  Audio  Output 、或 Audio  Output 的 Sampling frequency 或 frame 的 number According to the length 与 Audio  enter 不一致，不能 use  Acoustic echo sound Eliminator 。" );
                            }
                            break;
                        }
                    }

                    //创建并初始化 noise sound Suppressor 对象。
                    switch( m_AudioInputPt.m_UseWhatNs )
                    {
                        case 0: //如果 Do not use  noise sound Suppressor 。
                        {
                            if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "媒体处理线程： Do not use  noise sound Suppressor 。" );
                            break;
                        }
                        case 1: //如果要 use Speex Preprocessor  noise sound торможение 。
                        {
                            if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "媒体处理线程：稍后 в 初始化Speex预处理器 Time 一起初始化Speex Preprocessor  noise sound торможение 。" );
                            break;
                        }
                        case 2: //如果要 use WebRtc Fixed-point version  noise sound Suppressor 。
                        {
                            m_AudioInputPt.m_WebRtcNsxPt = new WebRtcNsx();
                            if( m_AudioInputPt.m_WebRtcNsxPt.Init( m_AudioInputPt.m_SamplingRate, m_AudioInputPt.m_FrameLen, m_AudioInputPt.m_WebRtcNsxPolicyMode ) == 0 )
                            {
                                if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "媒体处理线程：创建并初始化WebRtc Fixed-point version  noise sound Suppressor 类对象success." );
                            }
                            else
                            {
                                m_AudioInputPt.m_WebRtcNsxPt = null;
                                if( m_IsPrintLogcat != 0 ) Log.e( m_CurClsNameStrPt, "媒体处理线程：创建并初始化WebRtc Fixed-point version  noise sound Suppressor 类对象失败。" );
                                break out;
                            }
                            break;
                        }
                        case 3: //如果要 use WebRtc Floating point version  noise sound Suppressor 。
                        {
                            m_AudioInputPt.m_WebRtcNsPt = new WebRtcNs();
                            if( m_AudioInputPt.m_WebRtcNsPt.Init( m_AudioInputPt.m_SamplingRate, m_AudioInputPt.m_FrameLen, m_AudioInputPt.m_WebRtcNsPolicyMode ) == 0 )
                            {
                                if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "媒体处理线程：创建并初始化WebRtc Floating point version  noise sound Suppressor 类对象success." );
                            }
                            else
                            {
                                m_AudioInputPt.m_WebRtcNsPt = null;
                                if( m_IsPrintLogcat != 0 ) Log.e( m_CurClsNameStrPt, "媒体处理线程：创建并初始化WebRtc Floating point version  noise sound Suppressor 类对象失败。" );
                                break out;
                            }
                            break;
                        }
                        case 4: //如果要 use RNNoise noise sound Suppressor 。
                        {
                            m_AudioInputPt.m_RNNoisePt = new RNNoise();
                            if( m_AudioInputPt.m_RNNoisePt.Init( m_AudioInputPt.m_SamplingRate, m_AudioInputPt.m_FrameLen ) == 0 )
                            {
                                if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "媒体处理线程：创建并初始化RNNoise noise sound Suppressor 类对象success." );
                            }
                            else
                            {
                                m_AudioInputPt.m_RNNoisePt = null;
                                if( m_IsPrintLogcat != 0 ) Log.e( m_CurClsNameStrPt, "媒体处理线程：创建并初始化RNNoise noise sound Suppressor 类对象失败。" );
                                break out;
                            }
                            break;
                        }
                    }

                    //创建并初始化Speex预处理器类对象。
                    if( ( m_AudioInputPt.m_UseWhatNs == 1 ) || ( m_AudioInputPt.m_IsUseSpeexPprocOther != 0 ) )
                    {
                        if( m_AudioInputPt.m_UseWhatNs != 1 )
                        {
                            m_AudioInputPt.m_SpeexPprocIsUseNs = 0;
                            m_AudioInputPt.m_SpeexPprocIsUseDereverb = 0;
                        }
                        if( m_AudioInputPt.m_IsUseSpeexPprocOther == 0 )
                        {
                            m_AudioInputPt.m_SpeexPprocIsUseVad = 0;
                            m_AudioInputPt.m_SpeexPprocIsUseAgc = 0;
                        }
                        m_AudioInputPt.m_SpeexPprocPt = new SpeexPproc();
                        if( m_AudioInputPt.m_SpeexPprocPt.Init( m_AudioInputPt.m_SamplingRate, m_AudioInputPt.m_FrameLen, m_AudioInputPt.m_SpeexPprocIsUseNs, m_AudioInputPt.m_SpeexPprocNoiseSupes, m_AudioInputPt.m_SpeexPprocIsUseDereverb, m_AudioInputPt.m_SpeexPprocIsUseVad, m_AudioInputPt.m_SpeexPprocVadProbStart, m_AudioInputPt.m_SpeexPprocVadProbCont, m_AudioInputPt.m_SpeexPprocIsUseAgc, m_AudioInputPt.m_SpeexPprocAgcLevel, m_AudioInputPt.m_SpeexPprocAgcIncrement, m_AudioInputPt.m_SpeexPprocAgcDecrement, m_AudioInputPt.m_SpeexPprocAgcMaxGain ) == 0 )
                        {
                            if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "媒体处理线程：创建并初始化Speex预处理器类对象success."  );
                        }
                        else
                        {
                            m_AudioInputPt.m_SpeexPprocPt = null;
                            if( m_IsPrintLogcat != 0 ) Log.e( m_CurClsNameStrPt, "媒体处理线程：创建并初始化Speex预处理器类对象失败。" );
                            break out;
                        }
                    }

                    //初始化 Encoder 对象。
                    switch( m_AudioInputPt.m_UseWhatEncoder )
                    {
                        case 0: //如果要 use PCM Raw data 。
                        {
                            if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "媒体处理线程： use PCM Raw data 。" );
                            break;
                        }
                        case 1: //如果要 use Speex Encoder 。
                        {
                            if( m_AudioInputPt.m_FrameLen != m_AudioInputPt.m_SamplingRate / 1000 * 20 )
                            {
                                if( m_IsPrintLogcat != 0 ) Log.e( m_CurClsNameStrPt, "媒体处理线程： frame 的 number According to the length 不 for 20 millisecond不能 use Speex Encoder 。" );
                                break out;
                            }
                            m_AudioInputPt.m_SpeexEncoderPt = new SpeexEncoder();
                            if( m_AudioInputPt.m_SpeexEncoderPt.Init( m_AudioInputPt.m_SamplingRate, m_AudioInputPt.m_SpeexEncoderUseCbrOrVbr, m_AudioInputPt.m_SpeexEncoderQuality, m_AudioInputPt.m_SpeexEncoderComplexity, m_AudioInputPt.m_SpeexEncoderPlcExpectedLossRate ) == 0 )
                            {
                                if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "媒体处理线程：创建并初始化Speex Encoder 类对象success." );
                            }
                            else
                            {
                                m_AudioInputPt.m_SpeexEncoderPt = null;
                                if( m_IsPrintLogcat != 0 ) Log.e( m_CurClsNameStrPt, "媒体处理线程：创建并初始化Speex Encoder 类对象失败。" );
                                break out;
                            }
                            break;
                        }
                        case 2: //如果要 use Opus Encoder 。
                        {
                            if( m_IsPrintLogcat != 0 ) Log.e( m_CurClsNameStrPt, "媒体处理线程：暂不支持 use Opus Encoder 。" );
                            break out;
                        }
                    }

                    //创建并初始化 Audio  enter Wavefile写入器类对象、 Audio  result Wavefile写入器类对象。
                    if( m_AudioInputPt.m_IsSaveAudioToFile != 0 )
                    {
                        m_AudioInputPt.m_AudioInputWaveFileWriterPt = new WaveFileWriter();
                        if( m_AudioInputPt.m_AudioInputWaveFileWriterPt.Init( m_AudioInputPt.m_AudioInputFileFullPathStrPt, ( short ) 1, m_AudioInputPt.m_SamplingRate, 16 ) == 0 )
                        {
                            if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "媒体处理线程：创建并初始化 Audio  enter file " + m_AudioInputPt.m_AudioInputFileFullPathStrPt + " 的Wavefile写入器类对象success." );
                        }
                        else
                        {
                            m_AudioInputPt.m_AudioInputWaveFileWriterPt = null;
                            if( m_IsPrintLogcat != 0 ) Log.e( m_CurClsNameStrPt, "媒体处理线程：创建并初始化 Audio  enter file " + m_AudioInputPt.m_AudioInputFileFullPathStrPt + " 的Wavefile写入器类对象失败。" );
                            break out;
                        }
                        m_AudioInputPt.m_AudioResultWaveFileWriterPt = new WaveFileWriter();
                        if( m_AudioInputPt.m_AudioResultWaveFileWriterPt.Init( m_AudioInputPt.m_AudioResultFileFullPathStrPt, ( short ) 1, m_AudioInputPt.m_SamplingRate, 16 ) == 0 )
                        {
                            if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "媒体处理线程：创建并初始化 Audio  result file " + m_AudioInputPt.m_AudioResultFileFullPathStrPt + " 的Wavefile写入器类对象success." );
                        }
                        else
                        {
                            m_AudioInputPt.m_AudioResultWaveFileWriterPt = null;
                            if( m_IsPrintLogcat != 0 ) Log.e( m_CurClsNameStrPt, "媒体处理线程：创建并初始化 Audio  result file " + m_AudioInputPt.m_AudioResultFileFullPathStrPt + " 的Wavefile写入器类对象失败。" );
                            break out;
                        }
                    }

                    //创建并初始化 Audio input device类对象。
                    try
                    {
                        m_AudioInputPt.m_AudioInputDeviceBufSz = AudioRecord.getMinBufferSize( m_AudioInputPt.m_SamplingRate, AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT );
                        m_AudioInputPt.m_AudioInputDeviceBufSz = ( m_AudioInputPt.m_AudioInputDeviceBufSz > m_AudioInputPt.m_FrameLen * 2 ) ? m_AudioInputPt.m_AudioInputDeviceBufSz : m_AudioInputPt.m_FrameLen * 2;
                        m_AudioInputPt.m_AudioInputDevicePt = new AudioRecord(
                                ( m_AudioInputPt.m_IsUseSystemAecNsAgc != 0 ) ? ( ( android.os.Build.VERSION.SDK_INT >= 11 ) ? MediaRecorder.AudioSource.VOICE_COMMUNICATION : MediaRecorder.AudioSource.MIC ) : MediaRecorder.AudioSource.MIC,
                                m_AudioInputPt.m_SamplingRate,
                                AudioFormat.CHANNEL_CONFIGURATION_MONO,
                                AudioFormat.ENCODING_PCM_16BIT,
                                m_AudioInputPt.m_AudioInputDeviceBufSz
                        );
                        if( m_AudioInputPt.m_AudioInputDevicePt.getState() == AudioRecord.STATE_INITIALIZED )
                        {
                            if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "媒体处理线程：创建并初始化 Audio input device类对象success. Audio input device缓冲区大小：" + m_AudioInputPt.m_AudioInputDeviceBufSz );
                        }
                        else
                        {
                            if( m_IsPrintLogcat != 0 ) Log.e( m_CurClsNameStrPt, "媒体处理线程：创建并初始化 Audio input device类对象失败。" );
                            break out;
                        }
                    }
                    catch( IllegalArgumentException e )
                    {
                        if( m_IsPrintLogcat != 0 ) Log.e( m_CurClsNameStrPt, "媒体处理线程：创建并初始化 Audio input device类对象失败。原因：" + e.getMessage() );
                        break out;
                    }

                    //创建并初始化 Audio  enter  frame Linked list类对象。
                    m_AudioInputPt.m_AudioInputFrameLnkLstPt = new LinkedList< short[] >();
                    if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "媒体处理线程：创建并初始化 Audio  enter  frame Linked list类对象success." );

                    //创建并初始化 Audio  enter 空闲 frame Linked list类对象。
                    m_AudioInputPt.m_AudioInputIdleFrameLnkLstPt = new LinkedList< short[] >();
                    if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "媒体处理线程：创建并初始化 Audio  enter 空闲 frame Linked list类对象success." );

                    //创建并初始化 Audio  enter 线程类对象。
                    m_AudioInputPt.m_AudioInputThreadPt = new AudioInputThread();
                    if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "媒体处理线程：创建并初始化 Audio  enter 线程类对象success." );
                } //初始化 Audio  enter 完毕。

                //初始化 Audio  Output 。
                if( m_AudioOutputPt.m_IsUseAudioOutput != 0 ) //如果要 use  Audio  Output 。
                {
                    //初始化解码器对象。
                    switch( m_AudioOutputPt.m_UseWhatDecoder )
                    {
                        case 0: //如果要 use PCM Raw data 。
                        {
                            if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "媒体处理线程： use PCM Raw data 。" );
                            break;
                        }
                        case 1: //如果要 use Speex解码器。
                        {
                            if( m_AudioOutputPt.m_FrameLen != m_AudioOutputPt.m_SamplingRate / 1000 * 20 )
                            {
                                if( m_IsPrintLogcat != 0 ) Log.e( m_CurClsNameStrPt, "媒体处理线程： frame 的 number According to the length 不 for 20 millisecond不能 use Speex解码器。" );
                                break out;
                            }
                            m_AudioOutputPt.m_SpeexDecoderPt = new SpeexDecoder();
                            if( m_AudioOutputPt.m_SpeexDecoderPt.Init( m_AudioOutputPt.m_SamplingRate, m_AudioOutputPt.m_SpeexDecoderIsUsePerceptualEnhancement ) == 0 )
                            {
                                if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "媒体处理线程：创建并初始化Speex解码器类对象success." );
                            }
                            else
                            {
                                m_AudioOutputPt.m_SpeexDecoderPt = null;
                                if( m_IsPrintLogcat != 0 ) Log.e( m_CurClsNameStrPt, "媒体处理线程：创建并初始化Speex解码器类对象失败。" );
                                break out;
                            }
                            break;
                        }
                        case 2: //如果要 use Opus解码器。
                        {
                            if( m_IsPrintLogcat != 0 ) Log.e( m_CurClsNameStrPt, "媒体处理线程：暂不支持 use Opus解码器。" );
                            break out;
                        }
                    }

                    //创建并初始化 Audio  Output Wavefile写入器类对象。
                    if( m_AudioOutputPt.m_IsSaveAudioToFile != 0 )
                    {
                        m_AudioOutputPt.m_AudioOutputWaveFileWriterPt = new WaveFileWriter();
                        if( m_AudioOutputPt.m_AudioOutputWaveFileWriterPt.Init( m_AudioOutputPt.m_AudioOutputFileFullPathStrPt, ( short ) 1, m_AudioOutputPt.m_SamplingRate, 16 ) == 0 )
                        {
                            if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "媒体处理线程：创建并初始化 Audio  Output file " + m_AudioOutputPt.m_AudioOutputFileFullPathStrPt + " 的Wavefile写入器类对象success." );
                        }
                        else
                        {
                            m_AudioOutputPt.m_AudioOutputWaveFileWriterPt = null;
                            if( m_IsPrintLogcat != 0 ) Log.e( m_CurClsNameStrPt, "媒体处理线程：创建并初始化 Audio  Output file " + m_AudioOutputPt.m_AudioOutputFileFullPathStrPt + " 的Wavefile写入器类对象失败。" );
                            break out;
                        }
                    }

                    //Настраивать Audio  Output device 。
                    if( m_AudioOutputPt.m_UseWhatAudioOutputDevice == 0 ) //如果要 use speaker。
                    {
                        ( ( AudioManager )m_AppContextPt.getSystemService( Context.AUDIO_SERVICE ) ).setSpeakerphoneOn( true ); //打开speaker。
                    }
                    else //如果要 use earpiece。
                    {
                        ( ( AudioManager )m_AppContextPt.getSystemService( Context.AUDIO_SERVICE ) ).setSpeakerphoneOn( false ); //关闭speaker。
                    }

                    //用第一种方法创建并初始化 Audio  Output device 类对象。
                    try
                    {
                        m_AudioOutputPt.m_AudioOutputDeviceBufSz = m_AudioOutputPt.m_FrameLen * 2;
                        m_AudioOutputPt.m_AudioOutputDevicePt = new AudioTrack( ( m_AudioOutputPt.m_UseWhatAudioOutputStreamType == 0 ) ? AudioManager.STREAM_VOICE_CALL : AudioManager.STREAM_MUSIC,
                                m_AudioOutputPt.m_SamplingRate,
                                AudioFormat.CHANNEL_CONFIGURATION_MONO,
                                AudioFormat.ENCODING_PCM_16BIT,
                                m_AudioOutputPt.m_AudioOutputDeviceBufSz,
                                AudioTrack.MODE_STREAM );
                        if( m_AudioOutputPt.m_AudioOutputDevicePt.getState() == AudioTrack.STATE_INITIALIZED )
                        {
                            if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "媒体处理线程：用第一种方法创建并初始化 Audio  Output device 类对象success. Audio  Output device 缓冲区大小：" + m_AudioOutputPt.m_AudioOutputDeviceBufSz );
                        }
                        else
                        {
                            if( m_IsPrintLogcat != 0 ) Log.e( m_CurClsNameStrPt, "媒体处理线程：用第一种方法创建并初始化 Audio  Output device 类对象失败。" );
                            m_AudioOutputPt.m_AudioOutputDevicePt.release();
                            m_AudioOutputPt.m_AudioOutputDevicePt = null;
                        }
                    }
                    catch( IllegalArgumentException e )
                    {
                        if( m_IsPrintLogcat != 0 ) Log.e( m_CurClsNameStrPt, "媒体处理线程：用第一种方法创建并初始化 Audio  Output device 类对象失败。原因：" + e.getMessage() );
                    }

                    //用第二种方法创建并初始化 Audio  Output device 类对象。
                    if( m_AudioOutputPt.m_AudioOutputDevicePt == null )
                    {
                        try
                        {
                            m_AudioOutputPt.m_AudioOutputDeviceBufSz = AudioTrack.getMinBufferSize( m_AudioOutputPt.m_SamplingRate, AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT );
                            m_AudioOutputPt.m_AudioOutputDevicePt = new AudioTrack( ( m_AudioOutputPt.m_UseWhatAudioOutputStreamType == 0 ) ? AudioManager.STREAM_VOICE_CALL : AudioManager.STREAM_MUSIC,
                                    m_AudioOutputPt.m_SamplingRate,
                                    AudioFormat.CHANNEL_CONFIGURATION_MONO,
                                    AudioFormat.ENCODING_PCM_16BIT,
                                    m_AudioOutputPt.m_AudioOutputDeviceBufSz,
                                    AudioTrack.MODE_STREAM );
                            if( m_AudioOutputPt.m_AudioOutputDevicePt.getState() == AudioTrack.STATE_INITIALIZED )
                            {
                                if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "媒体处理线程：用第二种方法创建并初始化 Audio  Output device 类对象success. Audio  Output device 缓冲区大小：" + m_AudioOutputPt.m_AudioOutputDeviceBufSz );
                            }
                            else
                            {
                                if( m_IsPrintLogcat != 0 ) Log.e( m_CurClsNameStrPt, "媒体处理线程：用第二种方法创建并初始化 Audio  Output device 类对象失败。" );
                                break out;
                            }
                        }
                        catch( IllegalArgumentException e )
                        {
                            if( m_IsPrintLogcat != 0 ) Log.e( m_CurClsNameStrPt, "媒体处理线程：用第二种方法创建并初始化 Audio  Output device 类对象失败。原因：" + e.getMessage() );
                            break out;
                        }
                    }

                    //创建并初始化 Audio  Output  frame Linked list类对象。
                    m_AudioOutputPt.m_AudioOutputFrameLnkLstPt = new LinkedList< short[] >();
                    if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "媒体处理线程：创建并初始化 Audio  Output  frame Linked list类对象success." );

                    //创建并初始化 Audio  Output 空闲 frame Linked list类对象。
                    m_AudioOutputPt.m_AudioOutputIdleFrameLnkLstPt = new LinkedList< short[] >();
                    if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "媒体处理线程：创建并初始化 Audio  Output 空闲 frame Linked list类对象success." );

                    //创建并初始化 Audio  Output 线程类对象。
                    m_AudioOutputPt.m_AudioOutputThreadPt = new AudioOutputThread();
                    if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "媒体处理线程：创建并初始化 Audio  Output 线程类对象success." );
                } //初始化 Audio  Output 完毕。

                //初始化video enter 。
                if( m_VideoInputPt.m_IsUseVideoInput != 0 )
                {
                    //初始化 Encoder 对象。
                    switch( m_VideoInputPt.m_UseWhatEncoder )
                    {
                        case 0: //如果要 use YU12 Raw data 。
                        {
                            if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "媒体处理线程： use YU12 Raw data 。" );
                            break;
                        }
                        case 1: //如果要 use OpenH264 Encoder 。
                        {
                            m_VideoInputPt.m_OpenH264EncoderPt = new OpenH264Encoder();
                            if( m_VideoInputPt.m_OpenH264EncoderPt.Init( m_VideoInputPt.m_FrameWidth, m_VideoInputPt.m_FrameHeight, m_VideoInputPt.m_OpenH264EncoderVideoType, m_VideoInputPt.m_OpenH264EncoderEncodedBitrate, m_VideoInputPt.m_OpenH264EncoderBitrateControlMode, m_VideoInputPt.m_MaxSamplingRate, m_VideoInputPt.m_OpenH264EncoderIDRFrameIntvl, m_VideoInputPt.m_OpenH264EncoderComplexity, null ) == 0 )
                            {
                                if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "媒体处理线程：创建并初始化OpenH264 Encoder 类对象success." );
                            }
                            else
                            {
                                m_VideoInputPt.m_OpenH264EncoderPt = null;
                                if( m_IsPrintLogcat != 0 ) Log.e( m_CurClsNameStrPt, "媒体处理线程：创建并初始化OpenH264 Encoder 类对象失败。" );
                                break out;
                            }
                            break;
                        }
                    }

                    //创建video enter 线程类对象。
                    m_VideoInputPt.m_VideoInputThreadPt = new VideoInputThread();
                    if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "媒体处理线程：创建video enter 线程类对象success." );

                    //创建并初始化videoinput device类对象。
                    {
                        //打开videoinput device。
                        try
                        {
                            m_VideoInputPt.m_VideoInputDevicePt = Camera.open( m_VideoInputPt.m_UseWhatVideoInputDevice );
                        }
                        catch( RuntimeException e )
                        {
                            Log.e( m_CurClsNameStrPt, "媒体处理线程：创建并初始化videoinput device类对象失败。原因：" + e.getMessage() );
                            break out;
                        }

                        Camera.Parameters p_CameraParaPt = m_VideoInputPt.m_VideoInputDevicePt.getParameters(); //获取videoinput device的参 number 。

                        p_CameraParaPt.setPreviewFormat( ImageFormat.NV21 ); //Настраивать预览 frame 的格式。

                        p_CameraParaPt.setPreviewFrameRate( m_VideoInputPt.m_MaxSamplingRate ); //Настраивать最大 Sampling frequency 。

                        p_CameraParaPt.setPreviewSize( m_VideoInputPt.m_FrameHeight, m_VideoInputPt.m_FrameWidth ); //Настраивать预览 frame 的宽度 for Настраивать的high度，预览 frame 的high度 for Настраивать的宽度，因 for 预览 frame 处理的 Time 候要旋转。

                        List<String> p_FocusModesListPt = p_CameraParaPt.getSupportedFocusModes();
                        String p_PreviewFocusModePt = "";
                        for( p_TmpInt321 = 0; p_TmpInt321 < p_FocusModesListPt.size(); p_TmpInt321++ )
                        {
                            switch( p_FocusModesListPt.get( p_TmpInt321 ) )
                            {
                                case Camera.Parameters.FOCUS_MODE_AUTO: break; //自动对焦模式。应用程序应调用autoFocus（AutoFocusCallback）以此模式启动焦点。
                                case Camera.Parameters.FOCUS_MODE_INFINITY:
                                    if( !p_PreviewFocusModePt.equals( Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO ) &&
                                        !p_PreviewFocusModePt.equals( Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE ) &&
                                        !p_PreviewFocusModePt.equals( Camera.Parameters.FOCUS_MODE_EDOF ) &&
                                        !p_PreviewFocusModePt.equals( Camera.Parameters.FOCUS_MODE_FIXED ) )
                                        p_PreviewFocusModePt = Camera.Parameters.FOCUS_MODE_INFINITY; break; //焦点Настраивать в 无限远处。 в 这种模式下，应用程序不应调用autoFocus（AutoFocusCallback）。
                                case Camera.Parameters.FOCUS_MODE_MACRO: break; //微距（special写）对焦模式。应用程序应调用autoFocus（AutoFocusCallback）以此模式开始聚焦。
                                case Camera.Parameters.FOCUS_MODE_FIXED:
                                    if( !p_PreviewFocusModePt.equals( Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO ) &&
                                            !p_PreviewFocusModePt.equals( Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE ) &&
                                            !p_PreviewFocusModePt.equals( Camera.Parameters.FOCUS_MODE_EDOF ) )
                                        p_PreviewFocusModePt = Camera.Parameters.FOCUS_MODE_FIXED; break; //焦点是 fixed 的。如果焦点无法调节，则相机始终处于此模式。如果相机具 Have自动对焦，则此模式可以 fixed 焦点，通常处于ultra焦距。 в 这种模式下，应用程序不应调用autoFocus（AutoFocusCallback）。
                                case Camera.Parameters.FOCUS_MODE_EDOF:
                                    if( !p_PreviewFocusModePt.equals( Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO ) &&
                                            !p_PreviewFocusModePt.equals( Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE ) )
                                        p_PreviewFocusModePt = Camera.Parameters.FOCUS_MODE_EDOF; break; //扩展景深（EDOF），对焦以 number digit方式连续进行。 в 这种模式下，应用程序不应调用autoFocus（AutoFocusCallback）。
                                case Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO:
                                    p_PreviewFocusModePt = Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO; break; //用于video的连续自动对焦模式。
                                case Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE:
                                    if( !p_PreviewFocusModePt.equals( Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO ) )
                                        p_PreviewFocusModePt = Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE; break; //用于拍照的连续自动对焦模式，ratiovideo的连续自动对焦模式对焦速度更快。
                            }
                        }
                        p_CameraParaPt.setFocusMode( p_PreviewFocusModePt ); //Настраивать对焦模式。

                        try
                        {
                            m_VideoInputPt.m_VideoInputDevicePt.setParameters( p_CameraParaPt ); //Настраиватьvideoinput device的参 number 。
                        }
                        catch( RuntimeException e )
                        {
                            Log.e( m_CurClsNameStrPt, "媒体处理线程：创建并初始化videoinput device类对象失败。原因：" + e.getMessage() );
                            break out;
                        }

                        try
                        {
                            m_VideoInputPt.m_VideoInputDevicePt.setPreviewDisplay( m_VideoInputPt.m_VideoInputPreviewSurfaceViewPt.getHolder() ); //Настраиватьvideo enter 预览SurfaceView类对象。
                            m_VideoInputPt.m_VideoInputPreviewSurfaceViewPt.setWidthToHeightRatio( ( float )m_VideoInputPt.m_FrameWidth / m_VideoInputPt.m_FrameHeight ); //Настраиватьvideo enter 预览SurfaceView类对象的宽highratio。
                        }
                        catch( Exception ignored )
                        {
                        }
                        m_VideoInputPt.m_VideoInputDevicePt.setDisplayOrientation( 90 ); //调整相机拍到的图像旋转，不然竖着拿手机，图像是横着的。

                        //Настраиватьvideo enter 预览回调函 number 缓冲区的内存指针。
                        m_VideoInputPt.m_VideoInputPreviewCallbackBufferPtPt = new byte[ m_VideoInputPt.m_MaxSamplingRate ][ m_VideoInputPt.m_FrameWidth * m_VideoInputPt.m_FrameHeight * 3 / 2 ];
                        for( p_TmpInt321 = 0; p_TmpInt321 < m_VideoInputPt.m_MaxSamplingRate; p_TmpInt321++ )
                            m_VideoInputPt.m_VideoInputDevicePt.addCallbackBuffer( m_VideoInputPt.m_VideoInputPreviewCallbackBufferPtPt[p_TmpInt321] );

                        m_VideoInputPt.m_VideoInputDevicePt.setPreviewCallbackWithBuffer( m_VideoInputPt.m_VideoInputThreadPt ); //Настраиватьvideo enter 预览回调函 number 。

                        if( m_VideoInputPt.m_UseWhatVideoInputDevice == 1 ) //如果要 use Front camera。
                        {
                            m_VideoInputPt.m_VideoInputFrameRotate = 270; //Настраиватьvideo enter  frame 的旋转角度。
                        }
                        else //如果要 use rear camera。
                        {
                            m_VideoInputPt.m_VideoInputFrameRotate = 90; //Настраиватьvideo enter  frame 的旋转角度。
                        }

                        Log.i( m_CurClsNameStrPt, "媒体处理线程：创建并初始化videoinput device类对象success." );
                    }

                    //创建并初始化NV21格式video enter  frame Linked list类对象。
                    m_VideoInputPt.m_NV21VideoInputFrameLnkLstPt = new LinkedList< byte[] >();
                    if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "媒体处理线程：创建并初始化NV21格式video enter  frame Linked list类对象success." );

                    //创建并初始化video enter  frame Linked list类对象。
                    m_VideoInputPt.m_VideoInputFrameLnkLstPt = new LinkedList< VideoInputFrameElm >();
                    if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "媒体处理线程：创建并初始化video enter  frame Linked list类对象success." );

                    //创建并初始化video enter 空闲 frame Linked list类对象。
                    m_VideoInputPt.m_VideoInputIdleFrameLnkLstPt = new LinkedList< VideoInputFrameElm >();
                    if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "媒体处理线程：创建并初始化video enter 空闲 frame Linked list类对象success." );
                } //初始化video enter 完毕。

                //初始化video Output 。
                if( m_VideoOutputPt.m_IsUseVideoOutput != 0 )
                {
                    //初始化解码器对象。
                    switch( m_VideoOutputPt.m_UseWhatDecoder )
                    {
                        case 0: //如果要 use YU12 Raw data 。
                        {
                            if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "媒体处理线程： use YU12 Raw data 。" );
                            break;
                        }
                        case 1: //如果要 use OpenH264解码器。
                        {
                            m_VideoOutputPt.m_OpenH264DecoderPt = new OpenH264Decoder();
                            if( m_VideoOutputPt.m_OpenH264DecoderPt.Init( m_VideoOutputPt.m_OpenH264DecoderDecodeThreadNum, null ) == 0 )
                            {
                                if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "媒体处理线程：创建并初始化OpenH264解码器类对象success." );
                            }
                            else
                            {
                                m_VideoOutputPt.m_OpenH264DecoderPt = null;
                                if( m_IsPrintLogcat != 0 ) Log.e( m_CurClsNameStrPt, "媒体处理线程：创建并初始化OpenH264解码器类对象失败。" );
                                break out;
                            }
                            break;
                        }
                    }

                    //创建并初始化video Output  frame Linked list类对象。
                    m_VideoOutputPt.m_VideoOutputFrameLnkLstPt = new LinkedList< VideoOutputFrameElm >();
                    if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "媒体处理线程：创建并初始化video Output  frame Linked list类对象success." );

                    //创建并初始化video Output 空闲 frame Linked list类对象。
                    m_VideoOutputPt.m_VideoOutputIdleFrameLnkLstPt = new LinkedList< VideoOutputFrameElm >();
                    if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "媒体处理线程：创建并初始化video Output 空闲 frame Linked list类对象success." );

                    //创建video Output 线程类对象。
                    m_VideoOutputPt.m_VideoOutputThreadPt = new VideoOutputThread();
                    if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "媒体处理线程：创建video Output 线程类对象success." );
                } //初始化video Output 完毕。

                //启动 Audio  enter 线程、 Audio  Output 线程、video enter 线程、video Output 线程。必须 в 初始化最后启动这些线程，因 for 这些线程会 use 初始化 Time 的相关类对象。
                {
                    if( m_AudioInputPt.m_AudioInputThreadPt != null ) //如果要 use  Audio  enter 线程。
                    {
                        m_AudioInputPt.m_AudioInputThreadPt.start(); //启动 Audio  enter 线程，让 Audio  enter 线程再去启动 Audio  Output 线程。
                    }
                    else if( m_AudioOutputPt.m_AudioOutputDevicePt != null ) //如果要 use  Audio  Output 线程。
                    {
                        m_AudioOutputPt.m_AudioOutputDevicePt.play(); //让 Audio  Output device 类对象开始播放。
                        m_AudioOutputPt.m_AudioOutputThreadPt.start(); //启动 Audio  Output 线程。
                    }

                    if( m_VideoInputPt.m_VideoInputDevicePt != null ) //如果要 use videoinput device。
                    {
                        try
                        {
                            m_VideoInputPt.m_VideoInputDevicePt.startPreview(); //让videoinput device类对象开始预览。
                        }
                        catch( RuntimeException e )
                        {
                            Log.e( m_CurClsNameStrPt, "媒体处理线程：让videoinput device类对象开始预览失败。原因：" + e.getMessage() );
                            break out;
                        }
                        m_VideoInputPt.m_VideoInputThreadPt.start(); //启动video enter 线程。
                    }

                    if( m_VideoOutputPt.m_VideoOutputThreadPt != null ) //如果要 use video Output 线程。
                    {
                        m_VideoOutputPt.m_VideoOutputThreadPt.start(); //启动video Output 线程。
                    }
                }

                if( m_IsPrintLogcat != 0 )
                {
                    p_NowMsec = System.currentTimeMillis();
                    Log.i( m_CurClsNameStrPt, "媒体处理线程：媒体处理线程初始化完毕，耗 Time  " + ( p_NowMsec - p_LastMsec ) + "  millisecond，正式开始处理 frame 。" );
                }

                m_ExitCode = -2; //初始化已经成功了，再 will this 线程退出代码 Preset  for 处理失败，如果处理失败，这个退出代码就不用再Настраивать了，如果处理成功，再Настраивать for 成功的退出代码。
                m_RunFlag = RUN_FLAG_PROC; //Настраиватьthis 线程运行标记 for 初始化完毕正 в 循环处理 frame 。

                //开始soundvideo enter  Output  frame 处理循环。
                while( true )
                {
                    if( m_IsPrintLogcat != 0 ) p_LastMsec = System.currentTimeMillis();

                    //调用 user 定义的处理函 number 。
                    p_TmpInt321 = UserProcess();
                    if( p_TmpInt321 == 0 )
                    {
                        if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "媒体处理线程：调用 user 定义的处理函 number success.返回值：" + p_TmpInt321 );
                    }
                    else
                    {
                        if( m_IsPrintLogcat != 0 ) Log.e( m_CurClsNameStrPt, "媒体处理线程：调用 user 定义的处理函 number 失败。返回值：" + p_TmpInt321 );
                        break out;
                    }

                    if( m_IsPrintLogcat != 0 )
                    {
                        p_NowMsec = System.currentTimeMillis();
                        Log.i( m_CurClsNameStrPt, "媒体处理线程：调用 user 定义的处理函 number 完毕，耗 Time  " + ( p_NowMsec - p_LastMsec ) + "  millisecond。" );
                        p_LastMsec = System.currentTimeMillis();
                    }

                    //取出 Audio  enter  frame 和 Audio  Output  frame 。
                    if( m_AudioInputPt.m_AudioInputFrameLnkLstPt != null ) p_TmpInt321 = m_AudioInputPt.m_AudioInputFrameLnkLstPt.size(); //获取 Audio  enter  frame Linked list的元素个 number 。
                    else p_TmpInt321 = 0;
                    if( m_AudioOutputPt.m_AudioOutputFrameLnkLstPt != null ) p_TmpInt322 = m_AudioOutputPt.m_AudioOutputFrameLnkLstPt.size(); //获取 Audio  Output  frame Linked list的元素个 number 。
                    else p_TmpInt322 = 0;
                    if( ( p_TmpInt321 > 0 ) && ( p_TmpInt322 > 0 ) ) //如果 Audio  enter  frame Linked list和 Audio  Output  frame Linked listin都 Have frame 了，才开始取出。
                    {
                        //从 Audio  enter  frame Linked listin取出第一个 Audio  enter  frame 。
                        synchronized( m_AudioInputPt.m_AudioInputFrameLnkLstPt )
                        {
                            p_PcmAudioInputFramePt = m_AudioInputPt.m_AudioInputFrameLnkLstPt.getFirst();
                            m_AudioInputPt.m_AudioInputFrameLnkLstPt.removeFirst();
                        }
                        if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "媒体处理线程：从 Audio  enter  frame Linked listin取出第一个 Audio  enter  frame ， Audio  enter  frame Linked list元素个 number ：" + p_TmpInt321 + "。" );

                        //从 Audio  Output  frame Linked listin取出第一个 Audio  Output  frame 。
                        synchronized( m_AudioOutputPt.m_AudioOutputFrameLnkLstPt )
                        {
                            p_PcmAudioOutputFramePt = m_AudioOutputPt.m_AudioOutputFrameLnkLstPt.getFirst();
                            m_AudioOutputPt.m_AudioOutputFrameLnkLstPt.removeFirst();
                        }
                        if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "媒体处理线程：从 Audio  Output  frame Linked listin取出第一个 Audio  Output  frame ， Audio  Output  frame Linked list元素个 number ：" + p_TmpInt322 + "。" );

                        // will  Audio  enter  frame 复制到 Audio  result  frame ，方便处理。
                        System.arraycopy( p_PcmAudioInputFramePt, 0, p_PcmAudioResultFramePt, 0, p_PcmAudioInputFramePt.length );
                    }
                    else if( ( p_TmpInt321 > 0 ) && ( m_AudioOutputPt.m_AudioOutputFrameLnkLstPt == null ) ) //如果 Audio  enter  frame Linked list Have frame 了，且 Do not use  Audio  Output  frame Linked list，就开始取出。
                    {
                        //从 Audio  enter  frame Linked listin取出第一个 Audio  enter  frame 。
                        synchronized( m_AudioInputPt.m_AudioInputFrameLnkLstPt )
                        {
                            p_PcmAudioInputFramePt = m_AudioInputPt.m_AudioInputFrameLnkLstPt.getFirst();
                            m_AudioInputPt.m_AudioInputFrameLnkLstPt.removeFirst();
                        }
                        if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "媒体处理线程：从 Audio  enter  frame Linked listin取出第一个 Audio  enter  frame ， Audio  enter  frame Linked list元素个 number ：" + p_TmpInt321 + "。" );

                        // will  Audio  enter  frame 复制到 Audio  result  frame ，方便处理。
                        System.arraycopy( p_PcmAudioInputFramePt, 0, p_PcmAudioResultFramePt, 0, p_PcmAudioInputFramePt.length );
                    }
                    else if( ( p_TmpInt322 > 0 ) && ( m_AudioInputPt.m_AudioInputFrameLnkLstPt == null ) ) //如果 Audio  Output  frame Linked list Have frame 了，且 Do not use  Audio  enter  frame Linked list，就开始取出。
                    {
                        //从 Audio  Output  frame Linked listin取出第一个 Audio  Output  frame 。
                        synchronized( m_AudioOutputPt.m_AudioOutputFrameLnkLstPt )
                        {
                            p_PcmAudioOutputFramePt = m_AudioOutputPt.m_AudioOutputFrameLnkLstPt.getFirst();
                            m_AudioOutputPt.m_AudioOutputFrameLnkLstPt.removeFirst();
                        }
                        if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "媒体处理线程：从 Audio  Output  frame Linked listin取出第一个 Audio  Output  frame ， Audio  Output  frame Linked list元素个 number ：" + p_TmpInt322 + "。" );
                    }

                    //处理 Audio  enter  frame 。
                    if( p_PcmAudioInputFramePt != null )
                    {
                        // use  Acoustic echo sound Eliminator 。
                        switch( m_AudioInputPt.m_UseWhatAec )
                        {
                            case 0: //如果 Do not use  Acoustic echo sound Eliminator 。
                            {
                                if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "媒体处理线程： Do not use  Acoustic echo sound Eliminator 。" );
                                break;
                            }
                            case 1: //如果要 use Speex Acoustic echo sound Eliminator 。
                            {
                                if( ( m_AudioInputPt.m_SpeexAecPt != null ) && ( m_AudioInputPt.m_SpeexAecPt.Proc( p_PcmAudioResultFramePt, p_PcmAudioOutputFramePt, p_PcmAudioTmpFramePt ) == 0 ) )
                                {
                                    if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "媒体处理线程： use Speex Acoustic echo sound Eliminator success." );
                                    p_PcmAudioSwapFramePt = p_PcmAudioResultFramePt;p_PcmAudioResultFramePt = p_PcmAudioTmpFramePt;p_PcmAudioTmpFramePt = p_PcmAudioSwapFramePt; //交换 Audio  result  frame 和 Audio 临 Time  frame 。
                                }
                                else
                                {
                                    if( m_IsPrintLogcat != 0 ) Log.e( m_CurClsNameStrPt, "媒体处理线程： use Speex Acoustic echo sound Eliminator 失败。" );
                                }
                                break;
                            }
                            case 2: //如果要 use WebRtc Fixed-point version  Acoustic echo sound Eliminator 。
                            {
                                if( ( m_AudioInputPt.m_WebRtcAecmPt != null ) && ( m_AudioInputPt.m_WebRtcAecmPt.Proc( p_PcmAudioResultFramePt, p_PcmAudioOutputFramePt, p_PcmAudioTmpFramePt ) == 0 ) )
                                {
                                    if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "媒体处理线程： use WebRtc Fixed-point version  Acoustic echo sound Eliminator success." );
                                    p_PcmAudioSwapFramePt = p_PcmAudioResultFramePt;p_PcmAudioResultFramePt = p_PcmAudioTmpFramePt;p_PcmAudioTmpFramePt = p_PcmAudioSwapFramePt; //交换 Audio  result  frame 和 Audio 临 Time  frame 。
                                }
                                else
                                {
                                    if( m_IsPrintLogcat != 0 ) Log.e( m_CurClsNameStrPt, "媒体处理线程： use WebRtc Fixed-point version  Acoustic echo sound Eliminator 失败。" );
                                }
                                break;
                            }
                            case 3: //如果要 use WebRtc Floating point version  Acoustic echo sound Eliminator 。
                            {
                                if( ( m_AudioInputPt.m_WebRtcAecPt != null ) && ( m_AudioInputPt.m_WebRtcAecPt.Proc( p_PcmAudioResultFramePt, p_PcmAudioOutputFramePt, p_PcmAudioTmpFramePt ) == 0 ) )
                                {
                                    if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "媒体处理线程： use WebRtc Floating point version  Acoustic echo sound Eliminator success." );
                                    p_PcmAudioSwapFramePt = p_PcmAudioResultFramePt;p_PcmAudioResultFramePt = p_PcmAudioTmpFramePt;p_PcmAudioTmpFramePt = p_PcmAudioSwapFramePt; //交换 Audio  result  frame 和 Audio 临 Time  frame 。
                                }
                                else
                                {
                                    if( m_IsPrintLogcat != 0 ) Log.e( m_CurClsNameStrPt, "媒体处理线程： use WebRtc Floating point version  Acoustic echo sound Eliminator 失败。" );
                                }
                                break;
                            }
                            case 4: //如果要 use SpeexWebRtc triple  Acoustic echo sound Eliminator 。
                            {
                                if( ( m_AudioInputPt.m_SpeexWebRtcAecPt != null ) && ( m_AudioInputPt.m_SpeexWebRtcAecPt.Proc( p_PcmAudioResultFramePt, p_PcmAudioOutputFramePt, p_PcmAudioTmpFramePt ) == 0 ) )
                                {
                                    if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "媒体处理线程： use SpeexWebRtc triple  Acoustic echo sound Eliminator success." );
                                    p_PcmAudioSwapFramePt = p_PcmAudioResultFramePt;p_PcmAudioResultFramePt = p_PcmAudioTmpFramePt;p_PcmAudioTmpFramePt = p_PcmAudioSwapFramePt; //交换 Audio  result  frame 和 Audio 临 Time  frame 。
                                }
                                else
                                {
                                    if( m_IsPrintLogcat != 0 ) Log.e( m_CurClsNameStrPt, "媒体处理线程： use SpeexWebRtc triple  Acoustic echo sound Eliminator 失败。" );
                                }
                                break;
                            }
                        }

                        // use  noise sound Suppressor 。
                        switch( m_AudioInputPt.m_UseWhatNs )
                        {
                            case 0: //如果 Do not use  noise sound Suppressor 。
                            {
                                if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "媒体处理线程： Do not use  noise sound Suppressor 。" );
                                break;
                            }
                            case 1: //如果要 use Speex Preprocessor  noise sound торможение 。
                            {
                                if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "媒体处理线程：稍后 в  use Speex预处理器 Time 一起 use  noise sound торможение 。" );
                                break;
                            }
                            case 2: //如果要 use WebRtc Fixed-point version  noise sound Suppressor 。
                            {
                                if( m_AudioInputPt.m_WebRtcNsxPt.Proc( p_PcmAudioResultFramePt, p_PcmAudioTmpFramePt ) == 0 )
                                {
                                    if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "媒体处理线程： use WebRtc Fixed-point version  noise sound Suppressor success." );
                                    p_PcmAudioSwapFramePt = p_PcmAudioResultFramePt;p_PcmAudioResultFramePt = p_PcmAudioTmpFramePt;p_PcmAudioTmpFramePt = p_PcmAudioSwapFramePt; //交换 Audio  result  frame 和 Audio 临 Time  frame 。
                                }
                                else
                                {
                                    if( m_IsPrintLogcat != 0 ) Log.e( m_CurClsNameStrPt, "媒体处理线程： use WebRtc Fixed-point version  noise sound Suppressor 失败。" );
                                }
                                break;
                            }
                            case 3: //如果要 use WebRtc Floating point version  noise sound Suppressor 。
                            {
                                if( m_AudioInputPt.m_WebRtcNsPt.Proc( p_PcmAudioResultFramePt, p_PcmAudioTmpFramePt ) == 0 )
                                {
                                    if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "媒体处理线程： use WebRtc Floating point version  noise sound Suppressor success." );
                                    p_PcmAudioSwapFramePt = p_PcmAudioResultFramePt;p_PcmAudioResultFramePt = p_PcmAudioTmpFramePt;p_PcmAudioTmpFramePt = p_PcmAudioSwapFramePt; //交换 Audio  result  frame 和 Audio 临 Time  frame 。
                                }
                                else
                                {
                                    if( m_IsPrintLogcat != 0 ) Log.e( m_CurClsNameStrPt, "媒体处理线程： use WebRtc Floating point version  noise sound Suppressor 失败。" );
                                }
                                break;
                            }
                            case 4: //如果要 use RNNoise noise sound Suppressor 。
                            {
                                if( m_AudioInputPt.m_RNNoisePt.Proc( p_PcmAudioResultFramePt, p_PcmAudioTmpFramePt ) == 0 )
                                {
                                    if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "媒体处理线程： use RNNoise noise sound Suppressor success." );
                                    p_PcmAudioSwapFramePt = p_PcmAudioResultFramePt;p_PcmAudioResultFramePt = p_PcmAudioTmpFramePt;p_PcmAudioTmpFramePt = p_PcmAudioSwapFramePt; //交换 Audio  result  frame 和 Audio 临 Time  frame 。
                                }
                                else
                                {
                                    if( m_IsPrintLogcat != 0 ) Log.e( m_CurClsNameStrPt, "媒体处理线程： use RNNoise noise sound Suppressor 失败。" );
                                }
                                break;
                            }
                        }

                        // use Speex预处理器。
                        if( ( m_AudioInputPt.m_UseWhatNs == 1 ) || ( m_AudioInputPt.m_IsUseSpeexPprocOther != 0 ) )
                        {
                            if( m_AudioInputPt.m_SpeexPprocPt.Proc( p_PcmAudioResultFramePt, p_PcmAudioTmpFramePt, p_VoiceActStsPt ) == 0 )
                            {
                                if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "媒体处理线程： use Speex预处理器success.  voice sound live动状态：" + p_VoiceActStsPt.m_Val );
                                p_PcmAudioSwapFramePt = p_PcmAudioResultFramePt;p_PcmAudioResultFramePt = p_PcmAudioTmpFramePt;p_PcmAudioTmpFramePt = p_PcmAudioSwapFramePt; //交换 Audio  result  frame 和 Audio 临 Time  frame 。
                            }
                            else
                            {
                                if( m_IsPrintLogcat != 0 ) Log.e( m_CurClsNameStrPt, "媒体处理线程： use Speex预处理器失败。" );
                            }
                        }

                        //判断 Audio input device是否 Quiet sound。 в  Audio  enter 处理完后再Настраивать Quiet sound，这样可以保证 Audio  enter 处理器的连续性。
                        if( m_AudioInputPt.m_AudioInputDeviceIsMute != 0 )
                        {
                            Arrays.fill( p_PcmAudioResultFramePt, ( short ) 0 );
                            if( ( m_AudioInputPt.m_IsUseSpeexPprocOther != 0 ) && ( m_AudioInputPt.m_SpeexPprocIsUseVad != 0 ) ) //如果Speex预处理器要 use  Other functions ，且要 use   voice   sound activity detection 。
                            {
                                p_VoiceActStsPt.m_Val = 0;
                            }
                        }

                        // use  Encoder 。
                        switch( m_AudioInputPt.m_UseWhatEncoder )
                        {
                            case 0: //如果要 use PCM Raw data 。
                            {
                                if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "媒体处理线程： use PCM Raw data 。" );
                                break;
                            }
                            case 1: //如果要 use Speex Encoder 。
                            {
                                if( m_AudioInputPt.m_SpeexEncoderPt.Proc( p_PcmAudioResultFramePt, p_EncoderAudioInputFramePt, p_EncoderAudioInputFramePt.length, p_EncoderAudioInputFrameLenPt, p_EncoderAudioInputFrameIsNeedTransPt ) == 0 )
                                {
                                    if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "媒体处理线程： use Speex Encoder success.Speex格式 Audio  enter  frame 的 number According to the length ：" + p_EncoderAudioInputFrameLenPt.m_Val + "，Speex格式 Audio  enter  frame 是否需要传输：" + p_EncoderAudioInputFrameIsNeedTransPt.m_Val );
                                }
                                else
                                {
                                    if( m_IsPrintLogcat != 0 ) Log.e( m_CurClsNameStrPt, "媒体处理线程： use Speex Encoder 失败。" );
                                }
                                break;
                            }
                            case 2: //如果要 use Opus Encoder 。
                            {
                                if( m_IsPrintLogcat != 0 ) Log.e( m_CurClsNameStrPt, "媒体处理线程：暂不支持 use Opus Encoder 。" );
                                break out;
                            }
                        }

                        // use  Audio  enter Wavefile写入器写入 Audio  enter  frame  number 据、 Audio  result Wavefile写入器写入 Audio  result  frame  number 据。
                        if( m_AudioInputPt.m_IsSaveAudioToFile != 0 )
                        {
                            if( m_AudioInputPt.m_AudioInputWaveFileWriterPt.WriteData( p_PcmAudioInputFramePt, p_PcmAudioInputFramePt.length ) == 0 )
                            {
                                if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "媒体处理线程： use  Audio  enter Wavefile写入器写入 Audio  enter  frame success." );
                            }
                            else
                            {
                                if( m_IsPrintLogcat != 0 ) Log.e( m_CurClsNameStrPt, "媒体处理线程： use  Audio  enter Wavefile写入器写入 Audio  enter  frame 失败。" );
                            }
                            if( m_AudioInputPt.m_AudioResultWaveFileWriterPt.WriteData( p_PcmAudioResultFramePt, p_PcmAudioResultFramePt.length ) == 0 )
                            {
                                if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "媒体处理线程： use  Audio  result Wavefile写入器写入 Audio  result  frame success." );
                            }
                            else
                            {
                                if( m_IsPrintLogcat != 0 ) Log.e( m_CurClsNameStrPt, "媒体处理线程： use  Audio  result Wavefile写入器写入 Audio  result  frame 失败。" );
                            }
                        }
                    }

                    if( m_IsPrintLogcat != 0 )
                    {
                        p_NowMsec = System.currentTimeMillis();
                        Log.i( m_CurClsNameStrPt, "媒体处理线程：处理 Audio  enter  frame 完毕，耗 Time  " + ( p_NowMsec - p_LastMsec ) + "  millisecond。" );
                        p_LastMsec = System.currentTimeMillis();
                    }

                    //处理 Audio  Output  frame 。
                    if( p_PcmAudioOutputFramePt != null )
                    {
                        // use  Audio  Output Wavefile写入器写入 Output  frame  number 据。
                        if( m_AudioOutputPt.m_IsSaveAudioToFile != 0 )
                        {
                            if( m_AudioOutputPt.m_AudioOutputWaveFileWriterPt.WriteData( p_PcmAudioOutputFramePt, p_PcmAudioOutputFramePt.length ) == 0 )
                            {
                                if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "媒体处理线程： use  Audio  Output Wavefile写入器写入 Audio  Output  frame success." );
                            }
                            else
                            {
                                if( m_IsPrintLogcat != 0 ) Log.e( m_CurClsNameStrPt, "媒体处理线程： use  Audio  Output Wavefile写入器写入 Audio  Output  frame 失败。" );
                            }
                        }
                    }

                    if( m_IsPrintLogcat != 0 )
                    {
                        p_NowMsec = System.currentTimeMillis();
                        Log.i( m_CurClsNameStrPt, "媒体处理线程：处理 Audio  Output  frame 完毕，耗 Time  " + ( p_NowMsec - p_LastMsec ) + "  millisecond。" );
                        p_LastMsec = System.currentTimeMillis();
                    }

                    //处理video enter  frame 。
                    if( ( m_VideoInputPt.m_VideoInputFrameLnkLstPt != null ) && ( ( p_TmpInt321 = m_VideoInputPt.m_VideoInputFrameLnkLstPt.size() ) > 0 ) && //如果要 use video enter ，且video enter  frame Linked listin Have frame 了。
                        ( ( p_PcmAudioInputFramePt != null ) || ( m_AudioInputPt.m_AudioInputFrameLnkLstPt == null ) ) ) //且已经处理了 Audio  enter  frame 或 Do not use  Audio  enter  frame Linked list。
                    {
                        //从video enter  frame Linked listin取出第一个video enter  frame 。
                        synchronized( m_VideoInputPt.m_VideoInputFrameLnkLstPt )
                        {
                            p_VideoInputFramePt = m_VideoInputPt.m_VideoInputFrameLnkLstPt.getFirst();
                            m_VideoInputPt.m_VideoInputFrameLnkLstPt.removeFirst();
                        }
                        if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "媒体处理线程：从video enter  frame Linked listin取出第一个video enter  frame ，video enter  frame Linked list元素个 number ：" + p_TmpInt321 + "。" );
                    }

                    if( m_IsPrintLogcat != 0 )
                    {
                        p_NowMsec = System.currentTimeMillis();
                        Log.i( m_CurClsNameStrPt, "媒体处理线程：处理video enter  frame 完毕，耗 Time  " + ( p_NowMsec - p_LastMsec ) + "  millisecond。" );
                        p_LastMsec = System.currentTimeMillis();
                    }

                    //调用 user 定义的读取soundvideo enter  frame 函 number 。
                    if( ( p_PcmAudioInputFramePt != null ) || ( p_VideoInputFramePt != null ) )
                    {
                        if( p_VideoInputFramePt != null )
                            p_TmpInt321 = UserReadAudioVideoInputFrame( p_PcmAudioInputFramePt, p_PcmAudioResultFramePt, p_VoiceActStsPt, p_EncoderAudioInputFramePt, p_EncoderAudioInputFrameLenPt, p_EncoderAudioInputFrameIsNeedTransPt, p_VideoInputFramePt.m_RotateYU12VideoInputFramePt, p_VideoInputFramePt.m_RotateYU12VideoInputFrameWidthPt, p_VideoInputFramePt.m_RotateYU12VideoInputFrameHeightPt, p_VideoInputFramePt.m_EncoderVideoInputFramePt, p_VideoInputFramePt.m_EncoderVideoInputFrameLenPt );
                        else
                            p_TmpInt321 = UserReadAudioVideoInputFrame( p_PcmAudioInputFramePt, p_PcmAudioResultFramePt, p_VoiceActStsPt, p_EncoderAudioInputFramePt, p_EncoderAudioInputFrameLenPt, p_EncoderAudioInputFrameIsNeedTransPt, null, null, null, null, null );
                        if( p_TmpInt321 == 0 )
                        {
                            if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "媒体处理线程：调用 user 定义的读取soundvideo enter  frame 函 number success.返回值：" + p_TmpInt321 );
                        }
                        else
                        {
                            if( m_IsPrintLogcat != 0 ) Log.e( m_CurClsNameStrPt, "媒体处理线程：调用 user 定义的读取soundvideo enter  frame 函 number 失败。返回值：" + p_TmpInt321 );
                            break out;
                        }
                    }

                    if( m_IsPrintLogcat != 0 )
                    {
                        p_NowMsec = System.currentTimeMillis();
                        Log.i( m_CurClsNameStrPt, "媒体处理线程：调用 user 定义的读取soundvideo enter  frame 函 number 完毕，耗 Time  " + ( p_NowMsec - p_LastMsec ) + "  millisecond。" );
                        p_LastMsec = System.currentTimeMillis();
                    }

                    if( p_PcmAudioInputFramePt != null ) //追加this 次 Audio  enter  frame 到 Audio  enter 空闲 frame Linked list。
                    {
                        synchronized( m_AudioInputPt.m_AudioInputIdleFrameLnkLstPt )
                        {
                            m_AudioInputPt.m_AudioInputIdleFrameLnkLstPt.addLast( p_PcmAudioInputFramePt );
                        }
                        p_PcmAudioInputFramePt = null; //EmptyPCM格式 Audio  enter  frame 。
                    }
                    if( p_PcmAudioOutputFramePt != null ) //追加this 次 Audio  Output  frame 到 Audio  Output 空闲 frame Linked list。
                    {
                        synchronized( m_AudioOutputPt.m_AudioOutputIdleFrameLnkLstPt )
                        {
                            m_AudioOutputPt.m_AudioOutputIdleFrameLnkLstPt.addLast( p_PcmAudioOutputFramePt );
                        }
                        p_PcmAudioOutputFramePt = null; //EmptyPCM格式 Audio  Output  frame 。
                    }
                    if( p_VideoInputFramePt != null ) //追加this 次video enter  frame 到video enter 空闲 frame Linked list。
                    {
                        synchronized( m_VideoInputPt.m_VideoInputIdleFrameLnkLstPt )
                        {
                            m_VideoInputPt.m_VideoInputIdleFrameLnkLstPt.addLast( p_VideoInputFramePt );
                        }
                        p_VideoInputFramePt = null; //Emptyvideo enter  frame 。
                    }

                    if( m_ExitFlag != 0 ) //如果this 线程退出标记 for 请求退出。
                    {
                        m_ExitCode = 0; //处理已经成功了，再 will this 线程退出代码Настраивать for 正常退出。
                        if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "媒体处理线程：接收到退出请求，开始准备退出。" );
                        break out;
                    }

                    SystemClock.sleep( 1 ); //暂停一下，避免CPU use  rate 过high。
                } //soundvideo enter  Output  frame 处理循环完毕。
            }

            m_RunFlag = RUN_FLAG_DESTROY; //Настраиватьthis 线程运行标记 for 跳出循环处理 frame 正 в destroy。
            if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "媒体处理线程：this 线程开始退出。" );

            //请求soundvideo enter  Output 线程退出。必须 в destroysoundvideo enter  Output 前退出，因 for soundvideo enter  Output 线程会 use soundvideo enter  Output 相关类对象。
            if( m_AudioInputPt.m_AudioInputThreadPt != null ) m_AudioInputPt.m_AudioInputThreadPt.RequireExit(); //请求 Audio  enter 线程退出。
            if( m_AudioOutputPt.m_AudioOutputThreadPt != null ) m_AudioOutputPt.m_AudioOutputThreadPt.RequireExit(); //请求 Audio  Output 线程退出。
            if( m_VideoInputPt.m_VideoInputThreadPt != null ) m_VideoInputPt.m_VideoInputThreadPt.RequireExit(); //请求video enter 线程退出。
            if( m_VideoOutputPt.m_VideoOutputThreadPt != null ) m_VideoOutputPt.m_VideoOutputThreadPt.RequireExit(); //请求video Output 线程退出。

            //destroy Audio  enter 。
            {
                //destroy Audio  enter 线程类对象。
                if( m_AudioInputPt.m_AudioInputThreadPt != null )
                {
                    try
                    {
                        m_AudioInputPt.m_AudioInputThreadPt.join(); //等待 Audio  enter 线程退出。
                    }
                    catch( InterruptedException ignored )
                    {
                    }
                    m_AudioInputPt.m_AudioInputThreadPt = null;
                    if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "媒体处理线程：destroy Audio  enter 线程类对象success." );
                }

                //destroy Audio  enter 空闲 frame Linked list类对象。
                if( m_AudioInputPt.m_AudioInputIdleFrameLnkLstPt != null )
                {
                    m_AudioInputPt.m_AudioInputIdleFrameLnkLstPt.clear();
                    m_AudioInputPt.m_AudioInputIdleFrameLnkLstPt = null;
                    if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "媒体处理线程：destroy Audio  enter 空闲 frame Linked list类对象success." );
                }

                //destroy Audio  enter  frame Linked list类对象。
                if( m_AudioInputPt.m_AudioInputFrameLnkLstPt != null )
                {
                    m_AudioInputPt.m_AudioInputFrameLnkLstPt.clear();
                    m_AudioInputPt.m_AudioInputFrameLnkLstPt = null;
                    if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "媒体处理线程：destroy Audio  enter  frame Linked list类对象success." );
                }

                //destroy Audio input device类对象。
                if( m_AudioInputPt.m_AudioInputDevicePt != null )
                {
                    if( m_AudioInputPt.m_AudioInputDevicePt.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING ) m_AudioInputPt.m_AudioInputDevicePt.stop();
                    m_AudioInputPt.m_AudioInputDevicePt.release();
                    m_AudioInputPt.m_AudioInputDevicePt = null;
                    if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "媒体处理线程：destroy Audio input device类对象success." );
                }

                //destroy Audio  enter Wavefile写入器类对象、 Audio  result Wavefile写入器类对象。
                if( m_AudioInputPt.m_IsSaveAudioToFile != 0 )
                {
                    if( m_AudioInputPt.m_AudioInputWaveFileWriterPt != null )
                    {
                        if( m_AudioInputPt.m_AudioInputWaveFileWriterPt.Destroy() == 0 )
                        {
                            if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "媒体处理线程：destroy Audio  enter Wavefile写入器类对象success." );
                        }
                        else
                        {
                            if( m_IsPrintLogcat != 0 ) Log.e( m_CurClsNameStrPt, "媒体处理线程：destroy Audio  enter Wavefile写入器类对象失败。" );
                        }
                        m_AudioInputPt.m_AudioInputWaveFileWriterPt = null;
                    }
                    if( m_AudioInputPt.m_AudioResultWaveFileWriterPt != null )
                    {
                        if( m_AudioInputPt.m_AudioResultWaveFileWriterPt.Destroy() == 0 )
                        {
                            if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "媒体处理线程：destroy Audio  result Wavefile写入器类对象success." );
                        }
                        else
                        {
                            if( m_IsPrintLogcat != 0 ) Log.e( m_CurClsNameStrPt, "媒体处理线程：destroy Audio  result Wavefile写入器类对象失败。" );
                        }
                        m_AudioInputPt.m_AudioResultWaveFileWriterPt = null;
                    }
                }

                //destroy Encoder 类对象。
                switch( m_AudioInputPt.m_UseWhatEncoder )
                {
                    case 0: //如果要 use PCM Raw data 。
                    {
                        break;
                    }
                    case 1: //如果要 use Speex Encoder 。
                    {
                        if( m_AudioInputPt.m_SpeexEncoderPt != null )
                        {
                            if( m_AudioInputPt.m_SpeexEncoderPt.Destroy() == 0 )
                            {
                                if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "媒体处理线程：destroySpeex Encoder 类对象success." );
                            }
                            else
                            {
                                if( m_IsPrintLogcat != 0 ) Log.e( m_CurClsNameStrPt, "媒体处理线程：destroySpeex Encoder 类对象失败。" );
                            }
                            m_AudioInputPt.m_SpeexEncoderPt = null;
                        }
                        break;
                    }
                    case 2: //如果要 use Opus Encoder 。
                    {
                        break;
                    }
                }

                //destroySpeex预处理器类对象。
                if( m_AudioInputPt.m_SpeexPprocPt != null )
                {
                    if( m_AudioInputPt.m_SpeexPprocPt.Destroy() == 0 )
                    {
                        if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "媒体处理线程：destroySpeex预处理器类对象success." );
                    }
                    else
                    {
                        if( m_IsPrintLogcat != 0 ) Log.e( m_CurClsNameStrPt, "媒体处理线程：destroySpeex预处理器类对象失败。" );
                    }
                    m_AudioInputPt.m_SpeexPprocPt = null;
                }

                //destroy noise sound Suppressor 类对象。
                switch( m_AudioInputPt.m_UseWhatNs )
                {
                    case 0: //如果 Do not use  noise sound Suppressor 。
                    {
                        break;
                    }
                    case 1: //如果要 use Speex Preprocessor  noise sound торможение 。
                    {
                        if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "媒体处理线程：之前 в destroySpeex预处理器 Time 一起destroySpeex Preprocessor  noise sound торможение 。" );
                        break;
                    }
                    case 2: //如果要 use WebRtc Fixed-point version  noise sound Suppressor 。
                    {
                        if( m_AudioInputPt.m_WebRtcNsxPt != null )
                        {
                            if( m_AudioInputPt.m_WebRtcNsxPt.Destroy() == 0 )
                            {
                                if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "媒体处理线程：destroyWebRtc Fixed-point version  noise sound Suppressor 类对象success." );
                            }
                            else
                            {
                                if( m_IsPrintLogcat != 0 ) Log.e( m_CurClsNameStrPt, "媒体处理线程：destroyWebRtc Fixed-point version  noise sound Suppressor 类对象失败。" );
                            }
                            m_AudioInputPt.m_WebRtcNsxPt = null;
                        }
                        break;
                    }
                    case 3: //如果要 use WebRtc Floating point version  noise sound Suppressor 类对象。
                    {
                        if( m_AudioInputPt.m_WebRtcNsPt != null )
                        {
                            if( m_AudioInputPt.m_WebRtcNsPt.Destroy() == 0 )
                            {
                                if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "媒体处理线程：destroyWebRtc Floating point version  noise sound Suppressor 类对象success." );
                            }
                            else
                            {
                                if( m_IsPrintLogcat != 0 ) Log.e( m_CurClsNameStrPt, "媒体处理线程：destroyWebRtc Floating point version  noise sound Suppressor 类对象失败。" );
                            }
                            m_AudioInputPt.m_WebRtcNsPt = null;
                        }
                        break;
                    }
                    case 4: //如果要 use RNNoise noise sound Suppressor 类对象。
                    {
                        if( m_AudioInputPt.m_RNNoisePt != null )
                        {
                            if( m_AudioInputPt.m_RNNoisePt.Destroy() == 0 )
                            {
                                if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "媒体处理线程：destroyRNNoise noise sound Suppressor 类对象success." );
                            }
                            else
                            {
                                if( m_IsPrintLogcat != 0 ) Log.e( m_CurClsNameStrPt, "媒体处理线程：destroyRNNoise noise sound Suppressor 类对象失败。" );
                            }
                            m_AudioInputPt.m_RNNoisePt = null;
                        }
                        break;
                    }
                }

                //destroy Acoustic echo sound Eliminator 。
                switch( m_AudioInputPt.m_UseWhatAec )
                {
                    case 0: //如果 Do not use  Acoustic echo sound Eliminator 。
                    {
                        break;
                    }
                    case 1: //如果要 use Speex Acoustic echo sound Eliminator 。
                    {
                        if( m_AudioInputPt.m_SpeexAecPt != null )
                        {
                            if( m_AudioInputPt.m_SpeexAecIsSaveMemFile != 0 )
                            {
                                if( m_AudioInputPt.m_SpeexAecPt.SaveMemFile( m_AudioInputPt.m_SamplingRate, m_AudioInputPt.m_FrameLen, m_AudioInputPt.m_SpeexAecFilterLen, m_AudioInputPt.m_SpeexAecIsUseRec, m_AudioInputPt.m_SpeexAecEchoMultiple, m_AudioInputPt.m_SpeexAecEchoCont, m_AudioInputPt.m_SpeexAecEchoSupes, m_AudioInputPt.m_SpeexAecEchoSupesAct, m_AudioInputPt.m_SpeexAecMemFileFullPathStrPt, null ) == 0 )
                                {
                                    if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "媒体处理线程： will Speex Acoustic echo sound Eliminator 内存块 Save 到指定的file " + m_AudioInputPt.m_SpeexAecMemFileFullPathStrPt + " success." );
                                }
                                else
                                {
                                    if( m_IsPrintLogcat != 0 ) Log.e( m_CurClsNameStrPt, "媒体处理线程： will Speex Acoustic echo sound Eliminator 内存块 Save 到指定的file " + m_AudioInputPt.m_SpeexAecMemFileFullPathStrPt + " 失败。" );
                                }
                            }
                            if( m_AudioInputPt.m_SpeexAecPt.Destroy() == 0 )
                            {
                                if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "媒体处理线程：destroySpeex Acoustic echo sound Eliminator 类对象success." );
                            }
                            else
                            {
                                if( m_IsPrintLogcat != 0 ) Log.e( m_CurClsNameStrPt, "媒体处理线程：destroySpeex Acoustic echo sound Eliminator 类对象失败。" );
                            }
                            m_AudioInputPt.m_SpeexAecPt = null;
                        }
                        break;
                    }
                    case 2: //如果要 use WebRtc Fixed-point version  Acoustic echo sound Eliminator 。
                    {
                        if( m_AudioInputPt.m_WebRtcAecmPt != null )
                        {
                            if( m_AudioInputPt.m_WebRtcAecmPt.Destroy() == 0 )
                            {
                                if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "媒体处理线程：destroyWebRtc Fixed-point version  Acoustic echo sound Eliminator 类对象success." );
                            }
                            else
                            {
                                if( m_IsPrintLogcat != 0 ) Log.e( m_CurClsNameStrPt, "媒体处理线程：destroyWebRtc Fixed-point version  Acoustic echo sound Eliminator 类对象失败。" );
                            }
                            m_AudioInputPt.m_WebRtcAecmPt = null;
                        }
                        break;
                    }
                    case 3: //如果要 use WebRtc Floating point version  Acoustic echo sound Eliminator 。
                    {
                        if( m_AudioInputPt.m_WebRtcAecPt != null )
                        {
                            if( m_AudioInputPt.m_WebRtcAecIsSaveMemFile != 0 )
                            {
                                if( m_AudioInputPt.m_WebRtcAecPt.SaveMemFile( m_AudioInputPt.m_SamplingRate, m_AudioInputPt.m_FrameLen, m_AudioInputPt.m_WebRtcAecEchoMode, m_AudioInputPt.m_WebRtcAecDelay, m_AudioInputPt.m_WebRtcAecIsUseDelayAgnosticMode, m_AudioInputPt.m_WebRtcAecIsUseExtdFilterMode, m_AudioInputPt.m_WebRtcAecIsUseRefinedFilterAdaptAecMode, m_AudioInputPt.m_WebRtcAecIsUseAdaptAdjDelay, m_AudioInputPt.m_WebRtcAecMemFileFullPathStrPt, null ) == 0 )
                                {
                                    if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "媒体处理线程： will WebRtc Floating point version  Acoustic echo sound Eliminator 内存块 Save 到指定的file " + m_AudioInputPt.m_WebRtcAecMemFileFullPathStrPt + " success." );
                                }
                                else
                                {
                                    if( m_IsPrintLogcat != 0 ) Log.e( m_CurClsNameStrPt, "媒体处理线程： will WebRtc Floating point version  Acoustic echo sound Eliminator 内存块 Save 到指定的file " + m_AudioInputPt.m_WebRtcAecMemFileFullPathStrPt + " 失败。" );
                                }
                            }
                            if( m_AudioInputPt.m_WebRtcAecPt.Destroy() == 0 )
                            {
                                if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "媒体处理线程：destroyWebRtc Floating point version  Acoustic echo sound Eliminator 类对象success." );
                            }
                            else
                            {
                                if( m_IsPrintLogcat != 0 ) Log.e( m_CurClsNameStrPt, "媒体处理线程：destroyWebRtc Floating point version  Acoustic echo sound Eliminator 类对象失败。" );
                            }
                            m_AudioInputPt.m_WebRtcAecPt = null;
                        }
                        break;
                    }
                    case 4: //如果要 use SpeexWebRtc triple  Acoustic echo sound Eliminator 。
                    {
                        if( m_AudioInputPt.m_SpeexWebRtcAecPt != null )
                        {
                            if( m_AudioInputPt.m_SpeexWebRtcAecPt.Destroy() == 0 )
                            {
                                if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "媒体处理线程：destroySpeexWebRtc triple  Acoustic echo sound Eliminator 类对象success." );
                            }
                            else
                            {
                                if( m_IsPrintLogcat != 0 ) Log.e( m_CurClsNameStrPt, "媒体处理线程：destroySpeexWebRtc triple  Acoustic echo sound Eliminator 类对象失败。" );
                            }
                            m_AudioInputPt.m_SpeexWebRtcAecPt = null;
                        }
                        break;
                    }
                }
            } //destroy Audio  enter 完毕。

            //destroy Audio  Output 。
            {
                //destroy Audio  Output 线程类对象。
                if( m_AudioOutputPt.m_AudioOutputThreadPt != null )
                {
                    try
                    {
                        m_AudioOutputPt.m_AudioOutputThreadPt.join(); //等待 Audio  Output 线程退出。
                    }
                    catch( InterruptedException ignored )
                    {
                    }
                    m_AudioOutputPt.m_AudioOutputThreadPt = null;
                    if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "媒体处理线程：destroy Audio  Output 线程类对象success." );
                }

                //destroy Audio  Output 空闲 frame Linked list类对象。
                if( m_AudioOutputPt.m_AudioOutputIdleFrameLnkLstPt != null )
                {
                    m_AudioOutputPt.m_AudioOutputIdleFrameLnkLstPt.clear();
                    m_AudioOutputPt.m_AudioOutputIdleFrameLnkLstPt = null;
                    if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "媒体处理线程：destroy Audio  Output 空闲 frame Linked list类对象success." );
                }

                //destroy Audio  Output  frame Linked list类对象。
                if( m_AudioOutputPt.m_AudioOutputFrameLnkLstPt != null )
                {
                    m_AudioOutputPt.m_AudioOutputFrameLnkLstPt.clear();
                    m_AudioOutputPt.m_AudioOutputFrameLnkLstPt = null;
                    if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "媒体处理线程：destroy Audio  Output  frame Linked list类对象success." );
                }

                //destroy Audio  Output device 类对象。
                if( m_AudioOutputPt.m_AudioOutputDevicePt != null )
                {
                    if( m_AudioOutputPt.m_AudioOutputDevicePt.getPlayState() != AudioTrack.PLAYSTATE_STOPPED ) m_AudioOutputPt.m_AudioOutputDevicePt.stop();
                    m_AudioOutputPt.m_AudioOutputDevicePt.release();
                    m_AudioOutputPt.m_AudioOutputDevicePt = null;
                    if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "媒体处理线程：destroy Audio  Output device 类对象success." );
                }

                //destroy Audio  Output Wavefile写入器类对象。
                if( m_AudioOutputPt.m_IsSaveAudioToFile != 0 )
                {
                    if( m_AudioOutputPt.m_AudioOutputWaveFileWriterPt != null )
                    {
                        if( m_AudioOutputPt.m_AudioOutputWaveFileWriterPt.Destroy() == 0 )
                        {
                            if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "媒体处理线程：destroy Audio  Output Wavefile写入器类对象success." );
                        }
                        else
                        {
                            if( m_IsPrintLogcat != 0 ) Log.e( m_CurClsNameStrPt, "媒体处理线程：destroy Audio  Output Wavefile写入器类对象失败。" );
                        }
                        m_AudioOutputPt.m_AudioOutputWaveFileWriterPt = null;
                    }
                }

                //destroy解码器类对象。
                switch( m_AudioOutputPt.m_UseWhatDecoder )
                {
                    case 0: //如果要 use PCM Raw data 。
                    {
                        break;
                    }
                    case 1: //如果要 use Speex解码器。
                    {
                        if( m_AudioOutputPt.m_SpeexDecoderPt != null )
                        {
                            if( m_AudioOutputPt.m_SpeexDecoderPt.Destroy() == 0 )
                            {
                                if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "媒体处理线程：destroySpeex解码器类对象success." );
                            }
                            else
                            {
                                if( m_IsPrintLogcat != 0 ) Log.e( m_CurClsNameStrPt, "媒体处理线程：destroySpeex解码器类对象失败。" );
                            }
                            m_AudioOutputPt.m_SpeexDecoderPt = null;
                        }
                        break;
                    }
                    case 2: //如果要 use Opus解码器。
                    {
                        break;
                    }
                }
            } //destroy Audio  Output 完毕。

            //destroyvideo enter 。
            {
                //destroyvideo enter 线程类对象。
                if( m_VideoInputPt.m_VideoInputThreadPt != null )
                {
                    try
                    {
                        m_VideoInputPt.m_VideoInputThreadPt.join(); //等待video enter 线程退出。
                    }
                    catch( InterruptedException ignored )
                    {
                    }
                    m_VideoInputPt.m_VideoInputThreadPt = null;
                    if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "媒体处理线程：destroyvideo enter 线程类对象success." );
                }

                //destroyvideoinput device类对象。
                if( m_VideoInputPt.m_VideoInputDevicePt != null )
                {
                    m_VideoInputPt.m_VideoInputDevicePt.setPreviewCallback( null ); //Настраивать预览回调函 number  for 空，防止出现java.lang.RuntimeException: Method called after release()异常。
                    m_VideoInputPt.m_VideoInputDevicePt.stopPreview(); //停止预览。
                    m_VideoInputPt.m_VideoInputDevicePt.release(); //destroy摄像头。
                    m_VideoInputPt.m_VideoInputDevicePt = null;
                    m_VideoInputPt.m_VideoInputPreviewCallbackBufferPtPt = null;
                    if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "媒体处理线程：destroyvideoinput device类对象success." );
                }

                //destroyvideo enter 空闲 frame Linked list类对象。
                if( m_VideoInputPt.m_VideoInputIdleFrameLnkLstPt != null )
                {
                    m_VideoInputPt.m_VideoInputIdleFrameLnkLstPt.clear();
                    m_VideoInputPt.m_VideoInputIdleFrameLnkLstPt = null;
                    if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "媒体处理线程：destroyvideo enter 空闲 frame Linked list类对象success." );
                }

                //destroyvideo enter  frame Linked list类对象。
                if( m_VideoInputPt.m_VideoInputFrameLnkLstPt != null )
                {
                    m_VideoInputPt.m_VideoInputFrameLnkLstPt.clear();
                    m_VideoInputPt.m_VideoInputFrameLnkLstPt = null;
                    if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "媒体处理线程：destroyvideo enter  frame Linked list类对象success." );
                }

                //destroyNV21格式video enter  frame Linked list类对象。
                if( m_VideoInputPt.m_NV21VideoInputFrameLnkLstPt != null )
                {
                    m_VideoInputPt.m_NV21VideoInputFrameLnkLstPt.clear();
                    m_VideoInputPt.m_NV21VideoInputFrameLnkLstPt = null;
                    if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "媒体处理线程：destroyNV21格式video enter  frame Linked list类对象success." );
                }

                //destroy Encoder 类对象。
                switch( m_VideoInputPt.m_UseWhatEncoder )
                {
                    case 0: //如果要 use YU12 Raw data 。
                    {
                        break;
                    }
                    case 1: //如果要 use OpenH264 Encoder 。
                    {
                        if( m_VideoInputPt.m_OpenH264EncoderPt != null )
                        {
                            if( m_VideoInputPt.m_OpenH264EncoderPt.Destroy( null ) == 0 )
                            {
                                if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "媒体处理线程：destroyOpenH264 Encoder 类对象success." );
                            }
                            else
                            {
                                if( m_IsPrintLogcat != 0 ) Log.e( m_CurClsNameStrPt, "媒体处理线程：destroyOpenH264 Encoder 类对象失败。" );
                            }
                            m_VideoInputPt.m_OpenH264EncoderPt = null;
                        }
                        break;
                    }
                }
            } //destroyvideo enter 完毕。

            //destroyvideo Output 。
            {
                //destroyvideo Output 线程类对象。
                if( m_VideoOutputPt.m_VideoOutputThreadPt != null )
                {
                    try
                    {
                        m_VideoOutputPt.m_VideoOutputThreadPt.join(); //等待video Output 线程退出。
                    }
                    catch( InterruptedException ignored )
                    {
                    }
                    m_VideoOutputPt.m_VideoOutputThreadPt = null;
                    if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "媒体处理线程：destroyvideo Output 线程类对象success." );
                }

                //destroyvideo Output 空闲 frame Linked list类对象。
                if( m_VideoOutputPt.m_VideoOutputIdleFrameLnkLstPt != null )
                {
                    m_VideoOutputPt.m_VideoOutputIdleFrameLnkLstPt.clear();
                    m_VideoOutputPt.m_VideoOutputIdleFrameLnkLstPt = null;
                    if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "媒体处理线程：destroyvideo Output 空闲 frame Linked list类对象success." );
                }

                //destroyvideo Output  frame Linked list类对象。
                if( m_VideoOutputPt.m_VideoOutputFrameLnkLstPt != null )
                {
                    m_VideoOutputPt.m_VideoOutputFrameLnkLstPt.clear();
                    m_VideoOutputPt.m_VideoOutputFrameLnkLstPt = null;
                    if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "媒体处理线程：destroyvideo Output  frame Linked list类对象success." );
                }

                //destroy解码器类对象。
                switch( m_VideoOutputPt.m_UseWhatDecoder )
                {
                    case 0: //如果要 use YU12 Raw data 。
                    {
                        break;
                    }
                    case 1: //如果要 use OpenH264解码器。
                    {
                        if( m_VideoOutputPt.m_OpenH264DecoderPt != null )
                        {
                            if( m_VideoOutputPt.m_OpenH264DecoderPt.Destroy( null ) == 0 )
                            {
                                if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "媒体处理线程：destroyOpenH264解码器类对象success." );
                            }
                            else
                            {
                                if( m_IsPrintLogcat != 0 ) Log.e( m_CurClsNameStrPt, "媒体处理线程：destroyOpenH264解码器类对象失败。" );
                            }
                            m_VideoOutputPt.m_OpenH264DecoderPt = null;
                        }
                        break;
                    }
                }
            } //destroyvideo Output 完毕。

            //destroyPCM格式 Audio  enter  frame 、PCM格式 Audio  Output  frame 、PCM格式 Audio  result  frame 、PCM格式 Audio 临 Time  frame 、PCM格式 Audio 交换 frame 、  voice sound live动状态、已编码格式 Audio  enter  frame 、已编码格式 Audio  enter  frame 的 number According to the length 、已编码格式 Audio  enter  frame 是否需要传输、video enter  frame 。
            {
                p_PcmAudioInputFramePt = null;
                p_PcmAudioOutputFramePt = null;
                p_PcmAudioResultFramePt = null;
                p_PcmAudioTmpFramePt = null;
                p_PcmAudioSwapFramePt = null;
                p_VoiceActStsPt = null;
                p_EncoderAudioInputFramePt = null;
                p_EncoderAudioInputFrameLenPt = null;
                p_EncoderAudioInputFrameIsNeedTransPt = null;
                p_VideoInputFramePt = null;

                if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "媒体处理线程：destroyPCM格式 Audio  enter  frame 、PCM格式 Audio  Output  frame 、PCM格式 Audio  result  frame 、PCM格式 Audio 临 Time  frame 、PCM格式 Audio 交换 frame 、  voice sound live动状态、已编码格式 Audio  enter  frame 、已编码格式 Audio  enter  frame 的 number According to the length 、已编码格式 Audio  enter  frame 是否需要传输、video enter  frame 。" );
            }

            //destroy唤醒锁。
            WakeLockInitOrDestroy( 0 );

            if( m_ExitFlag != 3 ) //如果需要调用 user 定义的destroy函 number 。
            {
                UserDestroy(); //调用 user 定义的destroy函 number 。
                if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "媒体处理线程：调用 user 定义的destroy函 number success." );
            }

            m_RunFlag = RUN_FLAG_END; //Настраиватьthis 线程运行标记 for destroy完毕。

            if( ( m_ExitFlag == 0 ) || ( m_ExitFlag == 1 ) ) //如果 user 需要直接退出。
            {
                if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "媒体处理线程：this 线程已退出。" );
                break ReInit;
            }
            else //如果 user 需要重新初始化。
            {
                if( m_IsPrintLogcat != 0 ) Log.i( m_CurClsNameStrPt, "媒体处理线程：this 线程重新初始化。" );
            }
        }
    }
}
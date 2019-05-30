//================================================================================================================================
//
//  Copyright (c) 2015-2017 VisionStar Information Technology (Shanghai) Co., Ltd. All Rights Reserved.
//  EasyAR is the registered trademark or trademark of VisionStar Information Technology (Shanghai) Co., Ltd in China
//  and other countries for the augmented reality technology developed by VisionStar Information Technology (Shanghai) Co., Ltd.
//
//================================================================================================================================

package com.zhibo.duanshipin.AR;

import android.opengl.GLES20;
import android.util.Log;

import java.util.ArrayList;

import cn.easyar.CameraCalibration;
import cn.easyar.CameraDevice;
import cn.easyar.CameraDeviceFocusMode;
import cn.easyar.CameraDeviceType;
import cn.easyar.CameraFrameStreamer;
import cn.easyar.Frame;
import cn.easyar.FunctorOfVoidFromPointerOfTargetAndBool;
import cn.easyar.ImageTarget;
import cn.easyar.ImageTracker;
import cn.easyar.QRCodeScanner;
import cn.easyar.Renderer;
import cn.easyar.StorageType;
import cn.easyar.Target;
import cn.easyar.TargetInstance;
import cn.easyar.TargetStatus;
import cn.easyar.Vec2I;
import cn.easyar.Vec4I;

public class HelloAR
{
    private CameraDevice camera;
    private CameraFrameStreamer streamer;
    private ArrayList<ImageTracker> trackers;
    private Renderer videobg_renderer;
    private BoxRenderer box_renderer;
    private QRCodeScanner qrcode_scanner;
    private boolean viewport_changed = false;
    private Vec2I view_size = new Vec2I(0, 0);
    private int rotation = 0;
    private Vec4I viewport = new Vec4I(0, 0, 1280, 720);
    private int previous_qrcode_index = -1;
    private MessageAlerter onAlert;
    private int tracked_target = 0;
    private int active_target = 0;
    private ARVideo video = null;
    private ArrayList<VideoRenderer> video_renderers;
    private VideoRenderer current_video_renderer;

    public interface MessageAlerter
    {
        void invoke(String s);
    }

    public HelloAR()
    {
        trackers = new ArrayList<ImageTracker>();
    }

    private void loadFromImage(ImageTracker tracker, String path)
    {
        ImageTarget target = new ImageTarget();
        String jstr = "{\n"
            + "  \"images\" :\n"
            + "  [\n"
            + "    {\n"
            + "      \"image\" : \"" + path + "\",\n"
            + "      \"name\" : \"" + path.substring(0, path.indexOf(".")) + "\"\n"
            + "    }\n"
            + "  ]\n"
            + "}";
        target.setup(jstr, StorageType.Assets | StorageType.Json, "");
        tracker.loadTarget(target, new FunctorOfVoidFromPointerOfTargetAndBool() {
            @Override
            public void invoke(Target target, boolean status) {
                Log.i("HelloAR", String.format("load target (%b): %s (%d)", status, target.name(), target.runtimeID()));
            }
        });
    }

    private void loadFromJsonFile(ImageTracker tracker, String path, String targetname)
    {
        ImageTarget target = new ImageTarget();
        target.setup(path, StorageType.Assets, targetname);
        tracker.loadTarget(target, new FunctorOfVoidFromPointerOfTargetAndBool() {
            @Override
            public void invoke(Target target, boolean status) {
                Log.i("HelloAR", String.format("load target (%b): %s (%d)", status, target.name(), target.runtimeID()));
            }
        });
    }

    private void loadAllFromJsonFile(ImageTracker tracker, String path)
    {
        for (ImageTarget target : ImageTarget.setupAll(path, StorageType.Assets)) {
            tracker.loadTarget(target, new FunctorOfVoidFromPointerOfTargetAndBool() {
                @Override
                public void invoke(Target target, boolean status) {
                    Log.i("HelloAR", String.format("load target (%b): %s (%d)", status, target.name(), target.runtimeID()));
                }
            });
        }
    }

    public boolean initialize(MessageAlerter onAlert)
    {
        camera = new CameraDevice();
        streamer = new CameraFrameStreamer();
        streamer.attachCamera(camera);
        qrcode_scanner = new QRCodeScanner();
        qrcode_scanner.attachStreamer(streamer);
        this.onAlert = onAlert;

        boolean status = true;
        status &= camera.open(CameraDeviceType.Default);
        camera.setSize(new Vec2I(1280, 720));

        if (!status) { return status; }
        ImageTracker tracker = new ImageTracker();
        tracker.attachStreamer(streamer);
        //loadFromJsonFile(tracker, "targets.json", "argame");
        loadFromJsonFile(tracker, "targets.json", "idback");
        //loadAllFromJsonFile(tracker, "targets2.json");
        //loadFromImage(tracker, "namecard.jpg");
        loadFromImage(tracker, "postcard1.jpg");
        loadFromImage(tracker, "postcard2.jpg");
        loadFromImage(tracker, "postcard3.jpg");
        loadFromImage(tracker, "postcard4.jpg");
        trackers.add(tracker);

        return status;
    }

    public void dispose()
    {
        if (video != null) {
            video.dispose();
            video = null;
        }
        tracked_target = 0;
        active_target = 0;

        for (ImageTracker tracker : trackers) {
            tracker.dispose();
        }
        trackers.clear();
        box_renderer = null;
        if (videobg_renderer != null) {
            videobg_renderer.dispose();
            videobg_renderer = null;
        }
        if (qrcode_scanner != null) {
            qrcode_scanner.dispose();
            qrcode_scanner = null;
        }
        if (streamer != null) {
            streamer.dispose();
            streamer = null;
        }
        if (camera != null) {
            camera.dispose();
            camera = null;
        }
    }

    public boolean start()
    {
        boolean status = true;
        status &= (camera != null) && camera.start();
        status &= (streamer != null) && streamer.start();
        status &= (qrcode_scanner != null) && qrcode_scanner.start();
        camera.setFocusMode(CameraDeviceFocusMode.Continousauto);
        for (ImageTracker tracker : trackers) {
            status &= tracker.start();
        }
        return status;
    }

    public boolean stop()
    {
        boolean status = true;
        for (ImageTracker tracker : trackers) {
            status &= tracker.stop();
        }
        status &= (qrcode_scanner != null) && qrcode_scanner.stop();
        status &= (streamer != null) && streamer.stop();
        status &= (camera != null) && camera.stop();
        return status;
    }

    public void initGL()
    {
        if (videobg_renderer != null) {
            videobg_renderer.dispose();
        }
        videobg_renderer = new Renderer();
        box_renderer = new BoxRenderer();
        box_renderer.init();

        if (active_target != 0) {
            video.onLost();
            video.dispose();
            video  = null;
            tracked_target = 0;
            active_target = 0;
        }
        if (videobg_renderer != null) {
            videobg_renderer.dispose();
        }
        videobg_renderer = new Renderer();
        video_renderers = new ArrayList<VideoRenderer>();
        for (int k = 0; k < 5; k += 1) {
            VideoRenderer video_renderer = new VideoRenderer();
            video_renderer.init();
            video_renderers.add(video_renderer);
        }
        current_video_renderer = null;
    }

    public void resizeGL(int width, int height)
    {
        view_size = new Vec2I(width, height);
        viewport_changed = true;
    }

    private void updateViewport()
    {
        CameraCalibration calib = camera != null ? camera.cameraCalibration() : null;
        int rotation = calib != null ? calib.rotation() : 0;
        if (rotation != this.rotation) {
            this.rotation = rotation;
            viewport_changed = true;
        }
        if (viewport_changed) {
            Vec2I size = new Vec2I(1, 1);
            if ((camera != null) && camera.isOpened()) {
                size = camera.size();
            }
            if (rotation == 90 || rotation == 270) {
                size = new Vec2I(size.data[1], size.data[0]);
            }
            float scaleRatio = Math.max((float) view_size.data[0] / (float) size.data[0], (float) view_size.data[1] / (float) size.data[1]);
            Vec2I viewport_size = new Vec2I(Math.round(size.data[0] * scaleRatio), Math.round(size.data[1] * scaleRatio));
            viewport = new Vec4I((view_size.data[0] - viewport_size.data[0]) / 2, (view_size.data[1] - viewport_size.data[1]) / 2, viewport_size.data[0], viewport_size.data[1]);

            if ((camera != null) && camera.isOpened())
                viewport_changed = false;
        }
    }

    //private static String videoPath = "";
    public void render()
    {
        GLES20.glClearColor(1.f, 1.f, 1.f, 1.f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        if (videobg_renderer != null) {
            Vec4I default_viewport = new Vec4I(0, 0, view_size.data[0], view_size.data[1]);
            GLES20.glViewport(default_viewport.data[0], default_viewport.data[1], default_viewport.data[2], default_viewport.data[3]);
            if (videobg_renderer.renderErrorMessage(default_viewport)) {
                return;
            }
        }

        if (streamer == null) { return; }
        Frame frame = streamer.peek();
        try {
            updateViewport();
            GLES20.glViewport(viewport.data[0], viewport.data[1], viewport.data[2], viewport.data[3]);

            if (videobg_renderer != null) {
                videobg_renderer.render(frame, viewport);
            }

//            for (TargetInstance targetInstance : frame.targetInstances()) {
//                int status = targetInstance.status();
//                if (status == TargetStatus.Tracked) {
//                    Target target = targetInstance.target();
//                    ImageTarget imagetarget = target instanceof ImageTarget ? (ImageTarget)(target) : null;
//                    if (imagetarget == null) {
//                        continue;
//                    }
//                    if (box_renderer != null) {
//                        box_renderer.render(camera.projectionGL(0.2f, 500.f), targetInstance.poseGL(), imagetarget.size());
//                    }
//                }
//            }

            if (frame.index() != previous_qrcode_index) {
                previous_qrcode_index = frame.index();
                String text = frame.text();
                if (text != null && !text.equals("")) {
                    Log.i("HelloAR", "got qrcode: " + text);
                    onAlert.invoke("got qrcode: " + text);
                    //videoPath = text;
                }
            }

            //播放视频
            ArrayList<TargetInstance> targetInstances = frame.targetInstances();
            if (targetInstances.size() > 0) {
                TargetInstance targetInstance = targetInstances.get(0);
                Target target = targetInstance.target();
                int status = targetInstance.status();
                if (status == TargetStatus.Tracked) {
                    int id = target.runtimeID();
                    if (active_target != 0 && active_target != id) {
                        video.onLost();
                        video.dispose();
                        video  = null;
                        tracked_target = 0;
                        active_target = 0;
                    }
                    if (tracked_target == 0) {
                        if (video == null && video_renderers.size() > 0) {
                            String target_name = target.name();
//                            if (target_name.equals("argame") && video_renderers.get(0).texId() != 0) {
//                                video = new ARVideo();
//                                video.openVideoFile("video.mp4", video_renderers.get(0).texId());
//                                current_video_renderer = video_renderers.get(0);
//                            } else
                            if (target_name.equals("idback") && video_renderers.get(0).texId() != 0) {
                                video = new ARVideo();
                                video.openTransparentVideoFile("transparentvideo.mp4", video_renderers.get(0).texId());
                                current_video_renderer = video_renderers.get(0);
                            } //else if (target_name.equals("namecard") && video_renderers.get(2).texId() != 0) {
//                                video = new ARVideo();
//                                video.openStreamingVideo("http://gslb.miaopai.com/stream/ed5HCfnhovu3tyIQAiv60Q__.mp4", video_renderers.get(2).texId());
//                                current_video_renderer = video_renderers.get(2);
//                            }//明信片
                            else if (target_name.equals("postcard1") && video_renderers.get(1).texId() != 0) {
                                video = new ARVideo();
                                video.openStreamingVideo("http://vod.025nj.com/01zf.mp4", video_renderers.get(1).texId());
                                current_video_renderer = video_renderers.get(1);
                            } else if (target_name.equals("postcard2") && video_renderers.get(2).texId() != 0) {
                                video = new ARVideo();
                                video.openStreamingVideo("http://vod.025nj.com/02qh.mp4", video_renderers.get(2).texId());
                                current_video_renderer = video_renderers.get(2);
                            } else if (target_name.equals("postcard3") && video_renderers.get(3).texId() != 0) {
                                video = new ARVideo();
                                video.openStreamingVideo("http://vod.025nj.com/03mlxc.mp4", video_renderers.get(3).texId());
                                current_video_renderer = video_renderers.get(3);
                            } else if (target_name.equals("postcard4") && video_renderers.get(4).texId() != 0) {
                                video = new ARVideo();
                                video.openStreamingVideo("http://vod.025nj.com/04bes.mp4", video_renderers.get(4).texId());
                                current_video_renderer = video_renderers.get(4);
                            }
                        }
                        if (video != null) {
                            video.onFound();
                            tracked_target = id;
                            active_target = id;
                        }
                    }
                    ImageTarget imagetarget = target instanceof ImageTarget ? (ImageTarget)(target) : null;
                    if (imagetarget != null) {
                        if (current_video_renderer != null) {
                            video.update();
                            if (video.isRenderTextureAvailable()) {
                                current_video_renderer.render(camera.projectionGL(0.2f, 500.f), targetInstance.poseGL(), imagetarget.size());
                            }
                        }
                    }
                }
            } else {
                if (tracked_target != 0) {
                    video.onLost();
                    tracked_target = 0;
                }
            }
        }
        finally {
            frame.dispose();
        }
    }
}

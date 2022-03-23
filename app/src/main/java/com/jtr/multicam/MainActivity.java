package com.jtr.multicam;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "JTR";
    private static final String TAG2 = "Test";
    private Button takePictureButton;
    private Button openButton1;
    private TextureView textureView1;
    private TextureView textureView2;
    private TextureView textureView3;
    private TextureView textureView4;

    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    private String cameraId0;
    private String cameraId1;
    private String cameraId2;
    private String cameraId3;

    protected CameraDevice cameraDevice0;
    protected CameraDevice cameraDevice1;
    protected CameraDevice cameraDevice2;
    protected CameraDevice cameraDevice3;

    protected CameraCaptureSession cameraCaptureSessions0;
    protected CameraCaptureSession cameraCaptureSessions1;
    protected CameraCaptureSession cameraCaptureSessions2;
//    protected CameraCaptureSession cameraCaptureSessions;

    protected CaptureRequest captureRequest;
    protected CaptureRequest.Builder captureRequestBuilder0;
    protected CaptureRequest.Builder captureRequestBuilder1;
    protected CaptureRequest.Builder captureRequestBuilder2;
//    protected CaptureRequest.Builder captureRequestBuilder;

    private Size imageDimension;
    private ImageReader imageReader;
    private File file;
    private static final int REQUEST_CAMERA_PERMISSION = 200;
    private boolean mFlashSupported;
    private Handler mBackgroundHandler0;
    private Handler mBackgroundHandler1;
    private Handler mBackgroundHandler2;
    private HandlerThread mBackgroundThread0;
    private HandlerThread mBackgroundThread1;
    private HandlerThread mBackgroundThread2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textureView1 = (TextureView) findViewById(R.id.texture);
        textureView2 = (TextureView) findViewById(R.id.texture2);
        textureView3 = (TextureView) findViewById(R.id.texture3);
        textureView4 = (TextureView) findViewById(R.id.texture4);
        assert textureView1 != null;
        assert textureView2 != null;
        assert textureView3 != null;
        assert textureView4 != null;

        textureView1.setSurfaceTextureListener(textureListener0);
        textureView2.setSurfaceTextureListener(textureListener1);
        textureView3.setSurfaceTextureListener(textureListener2);

        openButton1 = (Button) findViewById(R.id.Open1);
        openButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "open button 1 click");
                openCamera1();
            }
        });

        takePictureButton = (Button) findViewById(R.id.btn_takepicture);
        assert takePictureButton != null;
        takePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePicture();
            }
        });
    }

    TextureView.SurfaceTextureListener textureListener0 = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            //open your camera here
            openCamera0();
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            // Transform you image captured size according to the surface width and height
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        }
    };

    TextureView.SurfaceTextureListener textureListener1 = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            //open your camera here
//            openCamera1();
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            // Transform you image captured size according to the surface width and height
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        }
    };

    TextureView.SurfaceTextureListener textureListener2 = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            //open your camera here
//            openCamera2();
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            // Transform you image captured size according to the surface width and height
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        }
    };

    private final CameraDevice.StateCallback stateCallback0 = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice camera) {
            //This is called when the camera is open
            Log.e(TAG, "0 onOpened");
            cameraDevice0 = camera;
            createCameraPreview0();
        }

        @Override
        public void onDisconnected(CameraDevice camera) {
            cameraDevice0.close();
        }

        @Override
        public void onError(CameraDevice camera, int error) {
            cameraDevice0.close();
            cameraDevice0 = null;
        }
    };

    private final CameraDevice.StateCallback stateCallback1 = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice camera) {
            //This is called when the camera is open
            Log.e(TAG, "1 onOpened");
            cameraDevice1 = camera;
            createCameraPreview1();
        }

        @Override
        public void onDisconnected(CameraDevice camera) {
            Log.e(TAG, "1 onDisconnected");
        }

        @Override
        public void onError(CameraDevice camera, int error) {
            Log.e(TAG, "1 onError");
            camera.close();
        }
    };

    private final CameraDevice.StateCallback stateCallback2 = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice camera) {
            //This is called when the camera is open
            Log.e(TAG, "2 onOpened");
            cameraDevice1 = camera;
            createCameraPreview2();
        }

        @Override
        public void onDisconnected(CameraDevice camera) {
            Log.e(TAG, "2 onDisconnected");
        }

        @Override
        public void onError(CameraDevice camera, int error) {
            Log.e(TAG, "2 onError");
            camera.close();
        }
    };

    final CameraCaptureSession.CaptureCallback captureCallbackListener = new CameraCaptureSession.CaptureCallback() {
        @Override
        public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
            super.onCaptureCompleted(session, request, result);
            Toast.makeText(MainActivity.this, "Saved:" + file, Toast.LENGTH_SHORT).show();
            createCameraPreview0();
        }
    };

    protected void startBackgroundThread0() {
        mBackgroundThread0 = new HandlerThread("Camera0 Background");
        mBackgroundThread0.start();
        mBackgroundHandler0 = new Handler(mBackgroundThread0.getLooper());
    }

    protected void stopBackgroundThread0() {
        mBackgroundThread0.quitSafely();
        try {
            mBackgroundThread0.join();
            mBackgroundThread0 = null;
            mBackgroundHandler0 = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    protected void startBackgroundThread1() {
        mBackgroundThread1 = new HandlerThread("Camera1 Background");
        mBackgroundThread1.start();
        mBackgroundHandler1 = new Handler(mBackgroundThread1.getLooper());
    }

    protected void stopBackgroundThread1() {
        mBackgroundThread1.quitSafely();
        try {
            mBackgroundThread1.join();
            mBackgroundThread1 = null;
            mBackgroundHandler1 = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    protected void startBackgroundThread2() {
        mBackgroundThread1 = new HandlerThread("Camera1 Background");
        mBackgroundThread1.start();
        mBackgroundHandler1 = new Handler(mBackgroundThread1.getLooper());
    }

    protected void stopBackgroundThread2() {
        mBackgroundThread1.quitSafely();
        try {
            mBackgroundThread1.join();
            mBackgroundThread1 = null;
            mBackgroundHandler1 = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    protected void takePicture() {
        if (null == cameraDevice0) {
            Log.e(TAG, "cameraDevice is null");
            return;
        }
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraDevice0.getId());
            Size[] jpegSizes = null;
            if (characteristics != null) {
                jpegSizes = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP).getOutputSizes(ImageFormat.JPEG);
            }
            int width = 640;
            int height = 480;
            if (jpegSizes != null && 0 < jpegSizes.length) {
                width = jpegSizes[0].getWidth();
                height = jpegSizes[0].getHeight();
            }
            ImageReader reader = ImageReader.newInstance(width, height, ImageFormat.JPEG, 1);
            List<Surface> outputSurfaces = new ArrayList<Surface>(2);
            outputSurfaces.add(reader.getSurface());
            outputSurfaces.add(new Surface(textureView1.getSurfaceTexture()));
            final CaptureRequest.Builder captureBuilder = cameraDevice0.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(reader.getSurface());
            captureBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
            // Orientation
            int rotation = getWindowManager().getDefaultDisplay().getRotation();
            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation));
            final File file = new File(Environment.getExternalStorageDirectory() + "/pic.jpg");
            ImageReader.OnImageAvailableListener readerListener = new ImageReader.OnImageAvailableListener() {
                @Override
                public void onImageAvailable(ImageReader reader) {
                    Image image = null;
                    try {
                        image = reader.acquireLatestImage();
                        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                        byte[] bytes = new byte[buffer.capacity()];
                        buffer.get(bytes);
                        save(bytes);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if (image != null) {
                            image.close();
                        }
                    }
                }

                private void save(byte[] bytes) throws IOException {
                    OutputStream output = null;
                    try {
                        output = new FileOutputStream(file);
                        output.write(bytes);
                    } finally {
                        if (null != output) {
                            output.close();
                        }
                    }
                }
            };
            reader.setOnImageAvailableListener(readerListener, mBackgroundHandler0);
            final CameraCaptureSession.CaptureCallback captureListener = new CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
                    super.onCaptureCompleted(session, request, result);
                    Toast.makeText(MainActivity.this, "Saved:" + file, Toast.LENGTH_SHORT).show();
                    createCameraPreview0();
                }
            };
            cameraDevice0.createCaptureSession(outputSurfaces, new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(CameraCaptureSession session) {
                    try {
                        session.capture(captureBuilder.build(), captureListener, mBackgroundHandler0);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed(CameraCaptureSession session) {
                }
            }, mBackgroundHandler0);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    protected void createCameraPreview0() {
        try {
            SurfaceTexture texture = textureView1.getSurfaceTexture();
            assert texture != null;
            texture.setDefaultBufferSize(imageDimension.getWidth(), imageDimension.getHeight());
            Surface surface = new Surface(texture);
            captureRequestBuilder0 = cameraDevice0.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureRequestBuilder0.addTarget(surface);
            cameraDevice0.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                    //The camera is already closed
                    if (null == cameraDevice0) {
                        return;
                    }
                    // When the session is ready, we start displaying the preview.
                    cameraCaptureSessions0 = cameraCaptureSession;
                    updatePreview0();
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                    Toast.makeText(MainActivity.this, "Configuration change", Toast.LENGTH_SHORT).show();
                }
            }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    protected void createCameraPreview1() {
        try {
            Log.v(TAG, "createCameraPreview1 1");
            SurfaceTexture texture = textureView2.getSurfaceTexture();
            Log.v(TAG, "createCameraPreview1 2");
            assert texture != null;
            Log.v(TAG, "createCameraPreview1 3");
            texture.setDefaultBufferSize(imageDimension.getWidth(), imageDimension.getHeight());
            Log.v(TAG, "createCameraPreview1 4");
            Surface surface = new Surface(texture);
            Log.v(TAG, "createCameraPreview1 5");
            captureRequestBuilder1 = cameraDevice1.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            Log.v(TAG, "createCameraPreview1 6");
            captureRequestBuilder1.addTarget(surface);
            Log.v(TAG, "createCameraPreview1 7");
            cameraDevice1.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                    //The camera is already closed
                    if (null == cameraDevice1) {
                        return;
                    }
                    // When the session is ready, we start displaying the preview.
                    cameraCaptureSessions1 = cameraCaptureSession;
                    updatePreview1();
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                    Toast.makeText(MainActivity.this, "Configuration change", Toast.LENGTH_SHORT).show();
                }
            }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    protected void createCameraPreview2() {
        try {
            SurfaceTexture texture = textureView3.getSurfaceTexture();
            assert texture != null;
            texture.setDefaultBufferSize(imageDimension.getWidth(), imageDimension.getHeight());
            Surface surface = new Surface(texture);
            captureRequestBuilder2 = cameraDevice2.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureRequestBuilder2.addTarget(surface);
            cameraDevice1.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                    //The camera is already closed
                    if (null == cameraDevice2) {
                        return;
                    }
                    // When the session is ready, we start displaying the preview.
                    cameraCaptureSessions1 = cameraCaptureSession;
                    updatePreview2();
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                    Toast.makeText(MainActivity.this, "Configuration change", Toast.LENGTH_SHORT).show();
                }
            }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void openCamera0() {
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        Log.e(TAG, "0 is camera open");
        String[] idList;
        try {
            idList = manager.getCameraIdList();
            Log.v(TAG2, "Id List: "  + Arrays.toString(idList));
            cameraId0 = manager.getCameraIdList()[0];
            Log.v(TAG2, "var cameraId0: " + cameraId0);
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId0);
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            assert map != null;
            imageDimension = map.getOutputSizes(SurfaceTexture.class)[0];
            // Add permission for camera and let user grant the permission
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CAMERA_PERMISSION);
                return;
            }
            manager.openCamera(cameraId0, stateCallback0, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        Log.e(TAG, "openCamera X");
    }

    private void openCamera1() {
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        Log.e(TAG, "openCamera1 E");
        try {
            Log.e(TAG, "openCamera1 1");
            cameraId1 = manager.getCameraIdList()[1];
            Log.e(TAG, "openCamera1 2");
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId1);
            Log.e(TAG, "openCamera1 3");

            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            Log.e(TAG, "openCamera1 4");
            assert map != null;
            imageDimension = map.getOutputSizes(SurfaceTexture.class)[0];
            // Add permission for camera and let user grant the permission
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CAMERA_PERMISSION);
                return;
            }
            Log.e(TAG, "openCamera1 5");
            manager.openCamera(cameraId1, stateCallback1, null);
            Log.e(TAG, "openCamera1 6");
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        Log.e(TAG, "openCamera X");
    }

    private void openCamera2() {
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        Log.e(TAG, "openCamera2 E");
        try {
            Log.e(TAG, "openCamera2 1");
            cameraId2 = manager.getCameraIdList()[2];
            Log.e(TAG, "openCamera2 2");
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId2);
            Log.e(TAG, "openCamera2 3");

            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            Log.e(TAG, "openCamera2 4");
            assert map != null;
            imageDimension = map.getOutputSizes(SurfaceTexture.class)[0];
            // Add permission for camera and let user grant the permission
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CAMERA_PERMISSION);
                return;
            }
            Log.e(TAG, "openCamera2 5");
            manager.openCamera(cameraId2, stateCallback2, null);
            Log.e(TAG, "openCamera2 6");
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        Log.e(TAG, "openCamera X");
    }

    protected void updatePreview0() {
        if (null == cameraDevice0) {
            Log.e(TAG, "updatePreview error, return");
        }
        captureRequestBuilder0.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
        try {
            cameraCaptureSessions0.setRepeatingRequest(captureRequestBuilder0.build(), null, mBackgroundHandler0);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    protected void updatePreview1() {
        if (null == cameraDevice1) {
            Log.e(TAG, "updatePreview error, return");
        }
        captureRequestBuilder1.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
        try {
            cameraCaptureSessions1.setRepeatingRequest(captureRequestBuilder1.build(), null, mBackgroundHandler1);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    protected void updatePreview2() {
        if (null == cameraDevice2) {
            Log.e(TAG, "updatePreview error, return");
        }
        captureRequestBuilder2.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
        try {
            cameraCaptureSessions2.setRepeatingRequest(captureRequestBuilder2.build(), null, mBackgroundHandler2);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void closeCamera() {
        if (null != cameraDevice0) {
            cameraDevice0.close();
            cameraDevice0 = null;
        }
        if (null != imageReader) {
            imageReader.close();
            imageReader = null;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                // close the app
                Toast.makeText(MainActivity.this, "Sorry!!!, you can't use this app without granting permission", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume");
        startBackgroundThread0();
        startBackgroundThread1();
        startBackgroundThread2();

        if (textureView1.isAvailable()) {
            openCamera0();
        } else {
            textureView1.setSurfaceTextureListener(textureListener0);
        }
    }

    @Override
    protected void onPause() {
        Log.e(TAG, "onPause");
        //closeCamera();
        stopBackgroundThread0();
        stopBackgroundThread1();
        stopBackgroundThread2();
        super.onPause();
    }
}
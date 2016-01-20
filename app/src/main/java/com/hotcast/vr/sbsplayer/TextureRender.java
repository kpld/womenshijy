/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hotcast.vr.sbsplayer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

/**
 * Code for rendering a texture onto a surface using OpenGL ES 2.0.
 */
public class TextureRender {
	private static final String TAG = "TextureRender";
	private static final int FLOAT_SIZE_BYTES = 4;
	private static final int TRIANGLE_VERTICES_DATA_STRIDE_BYTES = 5 * FLOAT_SIZE_BYTES;
	private static final int TRIANGLE_VERTICES_DATA_POS_OFFSET = 0;
	private static final int TRIANGLE_VERTICES_DATA_UV_OFFSET = 3;
	private final float[] mTriangleVerticesData = {
			// X, Y, Z, U, V
			-1.0f, -1.0f, 0, 0.f, 0.f, 1.0f, -1.0f, 0, 1.f, 0.f, -1.0f, 1.0f, 0, 0.f, 1.f, 1.0f, 1.0f, 0, 1.f, 1.f, };
	private FloatBuffer mTriangleVertices;
	private static final String VERTEX_SHADER = "uniform mat4 uMVPMatrix;\n" + "uniform mat4 uSTMatrix;\n" + "attribute vec4 aPosition;\n" + "attribute vec4 aTextureCoord;\n"
			+ "varying vec2 vTextureCoord;\n" + "void main() {\n" + "  gl_Position = uMVPMatrix * aPosition;\n" + "  vTextureCoord = (uSTMatrix * aTextureCoord).xy;\n" + "}\n";
	private static final String FRAGMENT_SHADER = "#extension GL_OES_EGL_image_external : require\n" + "precision mediump float;\n" + // highp
																																		// here
																																		// doesn't
																																		// seem
																																		// to
																																		// matter
			"varying vec2 vTextureCoord;\n" + "uniform samplerExternalOES sTexture;\n" + "void main() {\n" + "  gl_FragColor = texture2D(sTexture, vTextureCoord);\n" + "}\n";
	private float[] mMVPMatrix = new float[16];
	private float[] mSTMatrix = new float[16];
	private int mProgram;
	private int mTextureID = -12345;
	private int muMVPMatrixHandle;
	private int muSTMatrixHandle;
	private int maPositionHandle;
	private int maTextureHandle;

	private FloatBuffer mTriangleVertices1;
	private FloatBuffer mTriangleVertices2;

	private float mTriangleVerticesData1[] = { 0.0F, -1F, 0.0F, 0.0F, 0.0F, 1.0F, -1F, 0.0F, 1.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 0.0F, 1.0F, 1.0F };
	private float mTriangleVerticesData2[] = { -1F, -1F, 0.0F, 0.0F, 0.0F, 0.0F, -1F, 0.0F, 1.0F, 0.0F, -1F, 1.0F, 0.0F, 0.0F, 1.0F, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F };

	private MediaPlayer mediaPlayer;
	private VideoSurfaceView videoSurfaceView;
	private boolean spliteScreen;

	public boolean isSpliteScreen() {
		return spliteScreen;
	}

	public void setSpliteScreen(boolean spliteScreen) {
		this.spliteScreen = spliteScreen;
		if(isSpliteScreen()){
			setOldData();
		}else{
			setSingleOldData();
		}
	}


	public TextureRender(MediaPlayer mediaPlayer, VideoSurfaceView videoSurfaceView) {
		this.mediaPlayer = mediaPlayer;
		this.videoSurfaceView = videoSurfaceView;
		if(isSpliteScreen()){
			setOldData();
		}else{
			setSingleOldData();
		}
		Matrix.setIdentityM(mSTMatrix, 0);
	}

	float f3,f4,f8;

	public void setOldData(){
		float f1 = 2 * mediaPlayer.getVideoWidth();
		float f2 = mediaPlayer.getVideoHeight();
		f3 = videoSurfaceView.getWidth();
		f4 = videoSurfaceView.getHeight();
		float f5 = 0.0f;
		if(f3<=0 && f4<=0){
			f8 = 0.244709f;
		}else{
			if(f3/f4>=f1/f2){
				f8 = f2 * (f3 / f1) / f4;
				float[] arrayOfFloat5 = new float[20];
				arrayOfFloat5[0] = (-1.0F + f5);
				arrayOfFloat5[1] = (f5 + -f8);
				arrayOfFloat5[2] = 0.0F;
				arrayOfFloat5[3] = 0.0F;
				arrayOfFloat5[4] = 0.0F;
				arrayOfFloat5[5] = (0.0F - f5);
				arrayOfFloat5[6] = (f5 + -f8);
				arrayOfFloat5[7] = 0.0F;
				arrayOfFloat5[8] = 1.0F;
				arrayOfFloat5[9] = 0.0F;
				arrayOfFloat5[10] = (-1.0F + f5);
				arrayOfFloat5[11] = (f8 - f5);
				arrayOfFloat5[12] = 0.0F;
				arrayOfFloat5[13] = 0.0F;
				arrayOfFloat5[14] = 1.0F;
				arrayOfFloat5[15] = (0.0F - f5);
				arrayOfFloat5[16] = (f8 - f5);
				arrayOfFloat5[17] = 0.0F;
				arrayOfFloat5[18] = 1.0F;
				arrayOfFloat5[19] = 1.0F;
				float[] arrayOfFloat6 = new float[20];
				arrayOfFloat6[0] = (0.0F + f5);
				arrayOfFloat6[1] = (f5 - f8);
				arrayOfFloat6[2] = 0.0F;
				arrayOfFloat6[3] = 0.0F;
				arrayOfFloat6[4] = 0.0F;
				arrayOfFloat6[5] = (1.0F - f5);
				arrayOfFloat6[6] = (f5 - f8);
				arrayOfFloat6[7] = 0.0F;
				arrayOfFloat6[8] = 1.0F;
				arrayOfFloat6[9] = 0.0F;
				arrayOfFloat6[10] = (0.0F + f5);
				arrayOfFloat6[11] = (f8 - f5);
				arrayOfFloat6[12] = 0.0F;
				arrayOfFloat6[13] = 0.0F;
				arrayOfFloat6[14] = 1.0F;
				arrayOfFloat6[15] = (1.0F - f5);
				arrayOfFloat6[16] = (f8 - f5);
				arrayOfFloat6[17] = 0.0F;
				arrayOfFloat6[18] = 1.0F;
				arrayOfFloat6[19] = 1.0F;
				this.mTriangleVerticesData1 = arrayOfFloat5;
				this.mTriangleVerticesData2 = arrayOfFloat6;
			}else{
				float f9 = (float)(f1 * (f4 / f2) / f3 / 2.0D);
				float[] arrayOfFloat7 = new float[20];
				arrayOfFloat7[0] = (-0.5F - f9);
				arrayOfFloat7[1] = -1.0F;
				arrayOfFloat7[2] = 0.0F;
				arrayOfFloat7[3] = 0.0F;
				arrayOfFloat7[4] = 0.0F;
				arrayOfFloat7[5] = (-0.5F + f9);
				arrayOfFloat7[6] = -1.0F;
				arrayOfFloat7[7] = 0.0F;
				arrayOfFloat7[8] = 1.0F;
				arrayOfFloat7[9] = 0.0F;
				arrayOfFloat7[10] = (-0.5F - f9);
				arrayOfFloat7[11] = 1.0F;
				arrayOfFloat7[12] = 0.0F;
				arrayOfFloat7[13] = 0.0F;
				arrayOfFloat7[14] = 1.0F;
				arrayOfFloat7[15] = (-0.5F + f9);
				arrayOfFloat7[16] = 1.0F;
				arrayOfFloat7[17] = 0.0F;
				arrayOfFloat7[18] = 1.0F;
				arrayOfFloat7[19] = 1.0F;
				float[] arrayOfFloat8 = new float[20];
				arrayOfFloat8[0] = (0.5F - f9);
				arrayOfFloat8[1] = -1.0F;
				arrayOfFloat8[2] = 0.0F;
				arrayOfFloat8[3] = 0.0F;
				arrayOfFloat8[4] = 0.0F;
				arrayOfFloat8[5] = (0.5F + f9);
				arrayOfFloat8[6] = -1.0F;
				arrayOfFloat8[7] = 0.0F;
				arrayOfFloat8[8] = 1.0F;
				arrayOfFloat8[9] = 0.0F;
				arrayOfFloat8[10] = (0.5F - f9);
				arrayOfFloat8[11] = 1.0F;
				arrayOfFloat8[12] = 0.0F;
				arrayOfFloat8[13] = 0.0F;
				arrayOfFloat8[14] = 1.0F;
				arrayOfFloat8[15] = (0.5F + f9);
				arrayOfFloat8[16] = 1.0F;
				arrayOfFloat8[17] = 0.0F;
				arrayOfFloat8[18] = 1.0F;
				arrayOfFloat8[19] = 1.0F;
				this.mTriangleVerticesData1 = arrayOfFloat7;
				this.mTriangleVerticesData2 = arrayOfFloat8;
			}


			f8 = f2 * (f3 / f1) / f4;
			float[] arrayOfFloat5 = new float[20];
			arrayOfFloat5[0] = (-1.0F + f5);
			arrayOfFloat5[1] = (f5 + -f8);
			arrayOfFloat5[2] = 0.0F;
			arrayOfFloat5[3] = 0.0F;
			arrayOfFloat5[4] = 0.0F;
			arrayOfFloat5[5] = (0.0F - f5);
			arrayOfFloat5[6] = (f5 + -f8);
			arrayOfFloat5[7] = 0.0F;
			arrayOfFloat5[8] = 1.0F;
			arrayOfFloat5[9] = 0.0F;
			arrayOfFloat5[10] = (-1.0F + f5);
			arrayOfFloat5[11] = (f8 - f5);
			arrayOfFloat5[12] = 0.0F;
			arrayOfFloat5[13] = 0.0F;
			arrayOfFloat5[14] = 1.0F;
			arrayOfFloat5[15] = (0.0F - f5);
			arrayOfFloat5[16] = (f8 - f5);
			arrayOfFloat5[17] = 0.0F;
			arrayOfFloat5[18] = 1.0F;
			arrayOfFloat5[19] = 1.0F;
			float[] arrayOfFloat6 = new float[20];
			arrayOfFloat6[0] = (0.0F + f5);
			arrayOfFloat6[1] = (f5 - f8);
			arrayOfFloat6[2] = 0.0F;
			arrayOfFloat6[3] = 0.0F;
			arrayOfFloat6[4] = 0.0F;
			arrayOfFloat6[5] = (1.0F - f5);
			arrayOfFloat6[6] = (f5 - f8);
			arrayOfFloat6[7] = 0.0F;
			arrayOfFloat6[8] = 1.0F;
			arrayOfFloat6[9] = 0.0F;
			arrayOfFloat6[10] = (0.0F + f5);
			arrayOfFloat6[11] = (f8 - f5);
			arrayOfFloat6[12] = 0.0F;
			arrayOfFloat6[13] = 0.0F;
			arrayOfFloat6[14] = 1.0F;
			arrayOfFloat6[15] = (1.0F - f5);
			arrayOfFloat6[16] = (f8 - f5);
			arrayOfFloat6[17] = 0.0F;
			arrayOfFloat6[18] = 1.0F;
			arrayOfFloat6[19] = 1.0F;
			this.mTriangleVerticesData1 = arrayOfFloat5;
			this.mTriangleVerticesData2 = arrayOfFloat6;

		}
//		float f6 = 0.25058687f;
//		f6 = f3 / (f1 * (f4 / f2)) / 2.0F;




		mTriangleVertices = ByteBuffer.allocateDirect(mTriangleVerticesData.length * FLOAT_SIZE_BYTES).order(ByteOrder.nativeOrder()).asFloatBuffer();
		mTriangleVertices.put(mTriangleVerticesData).position(0);

		mTriangleVertices1 = ByteBuffer.allocateDirect(FLOAT_SIZE_BYTES * mTriangleVerticesData1.length).order(ByteOrder.nativeOrder()).asFloatBuffer();
		mTriangleVertices1.put(mTriangleVerticesData1).position(0);
		mTriangleVertices2 = ByteBuffer.allocateDirect(FLOAT_SIZE_BYTES * mTriangleVerticesData2.length).order(ByteOrder.nativeOrder()).asFloatBuffer();
		mTriangleVertices2.put(mTriangleVerticesData2).position(0);
	}
	public void setSingleOldData(){
		float f1 = mediaPlayer.getVideoWidth();
		float f2 = mediaPlayer.getVideoHeight();
		f3 = videoSurfaceView.getWidth();
		f4 = videoSurfaceView.getHeight();
		float f5 = 0.0f;
		if(f3<=0 && f4<=0){
			f8 = 0.244709f;
		}else{
			if(f3/f4>=f1/f2){
				f8 = f2 * (f3 / f1) / f4;
				float[] arrayOfFloat5 = new float[20];
				arrayOfFloat5[0] = (-1.0F + f5);
				arrayOfFloat5[1] = (f5 + -f8);
				arrayOfFloat5[2] = 0.0F;
				arrayOfFloat5[3] = 0.0F;
				arrayOfFloat5[4] = 0.0F;
				arrayOfFloat5[5] = (0.0F - f5);
				arrayOfFloat5[6] = (f5 + -f8);
				arrayOfFloat5[7] = 0.0F;
				arrayOfFloat5[8] = 1.0F;
				arrayOfFloat5[9] = 0.0F;
				arrayOfFloat5[10] = (-1.0F + f5);
				arrayOfFloat5[11] = (f8 - f5);
				arrayOfFloat5[12] = 0.0F;
				arrayOfFloat5[13] = 0.0F;
				arrayOfFloat5[14] = 1.0F;
				arrayOfFloat5[15] = (0.0F - f5);
				arrayOfFloat5[16] = (f8 - f5);
				arrayOfFloat5[17] = 0.0F;
				arrayOfFloat5[18] = 1.0F;
				arrayOfFloat5[19] = 1.0F;
				float[] arrayOfFloat6 = new float[20];
				arrayOfFloat6[0] = (0.0F + f5);
				arrayOfFloat6[1] = (f5 - f8);
				arrayOfFloat6[2] = 0.0F;
				arrayOfFloat6[3] = 0.0F;
				arrayOfFloat6[4] = 0.0F;
				arrayOfFloat6[5] = (1.0F - f5);
				arrayOfFloat6[6] = (f5 - f8);
				arrayOfFloat6[7] = 0.0F;
				arrayOfFloat6[8] = 1.0F;
				arrayOfFloat6[9] = 0.0F;
				arrayOfFloat6[10] = (0.0F + f5);
				arrayOfFloat6[11] = (f8 - f5);
				arrayOfFloat6[12] = 0.0F;
				arrayOfFloat6[13] = 0.0F;
				arrayOfFloat6[14] = 1.0F;
				arrayOfFloat6[15] = (1.0F - f5);
				arrayOfFloat6[16] = (f8 - f5);
				arrayOfFloat6[17] = 0.0F;
				arrayOfFloat6[18] = 1.0F;
				arrayOfFloat6[19] = 1.0F;
				this.mTriangleVerticesData1 = arrayOfFloat5;
				this.mTriangleVerticesData2 = arrayOfFloat6;
			}else{
				float f9 = (float)(f1 * (f4 / f2) / f3 / 2.0D);
				float[] arrayOfFloat7 = new float[20];
				arrayOfFloat7[0] = (-0.5F - f9);
				arrayOfFloat7[1] = -1.0F;
				arrayOfFloat7[2] = 0.0F;
				arrayOfFloat7[3] = 0.0F;
				arrayOfFloat7[4] = 0.0F;
				arrayOfFloat7[5] = (-0.5F + f9);
				arrayOfFloat7[6] = -1.0F;
				arrayOfFloat7[7] = 0.0F;
				arrayOfFloat7[8] = 1.0F;
				arrayOfFloat7[9] = 0.0F;
				arrayOfFloat7[10] = (-0.5F - f9);
				arrayOfFloat7[11] = 1.0F;
				arrayOfFloat7[12] = 0.0F;
				arrayOfFloat7[13] = 0.0F;
				arrayOfFloat7[14] = 1.0F;
				arrayOfFloat7[15] = (-0.5F + f9);
				arrayOfFloat7[16] = 1.0F;
				arrayOfFloat7[17] = 0.0F;
				arrayOfFloat7[18] = 1.0F;
				arrayOfFloat7[19] = 1.0F;
				float[] arrayOfFloat8 = new float[20];
				arrayOfFloat8[0] = (0.5F - f9);
				arrayOfFloat8[1] = -1.0F;
				arrayOfFloat8[2] = 0.0F;
				arrayOfFloat8[3] = 0.0F;
				arrayOfFloat8[4] = 0.0F;
				arrayOfFloat8[5] = (0.5F + f9);
				arrayOfFloat8[6] = -1.0F;
				arrayOfFloat8[7] = 0.0F;
				arrayOfFloat8[8] = 1.0F;
				arrayOfFloat8[9] = 0.0F;
				arrayOfFloat8[10] = (0.5F - f9);
				arrayOfFloat8[11] = 1.0F;
				arrayOfFloat8[12] = 0.0F;
				arrayOfFloat8[13] = 0.0F;
				arrayOfFloat8[14] = 1.0F;
				arrayOfFloat8[15] = (0.5F + f9);
				arrayOfFloat8[16] = 1.0F;
				arrayOfFloat8[17] = 0.0F;
				arrayOfFloat8[18] = 1.0F;
				arrayOfFloat8[19] = 1.0F;
				this.mTriangleVerticesData1 = arrayOfFloat7;
				this.mTriangleVerticesData2 = arrayOfFloat8;
			}


			f8 = f2 * (f3 / f1) / f4;
			float[] arrayOfFloat5 = new float[20];
			arrayOfFloat5[0] = (-1.0F + f5);
			arrayOfFloat5[1] = (f5 + -f8);
			arrayOfFloat5[2] = 0.0F;
			arrayOfFloat5[3] = 0.0F;
			arrayOfFloat5[4] = 0.0F;
			arrayOfFloat5[5] = (1.0F - f5);
			arrayOfFloat5[6] = (f5 + -f8);
			arrayOfFloat5[7] = 0.0F;
			arrayOfFloat5[8] = 1.0F;
			arrayOfFloat5[9] = 0.0F;
			arrayOfFloat5[10] = (-1.0F + f5);
			arrayOfFloat5[11] = (f8 - f5);
			arrayOfFloat5[12] = 0.0F;
			arrayOfFloat5[13] = 0.0F;
			arrayOfFloat5[14] = 1.0F;
			arrayOfFloat5[15] = (1.0F - f5);
			arrayOfFloat5[16] = (f8 - f5);
			arrayOfFloat5[17] = 0.0F;
			arrayOfFloat5[18] = 1.0F;
			arrayOfFloat5[19] = 1.0F;
			this.mTriangleVerticesData1 = arrayOfFloat5;

		}
//		float f6 = 0.25058687f;
//		f6 = f3 / (f1 * (f4 / f2)) / 2.0F;




		mTriangleVertices = ByteBuffer.allocateDirect(mTriangleVerticesData.length * FLOAT_SIZE_BYTES).order(ByteOrder.nativeOrder()).asFloatBuffer();
		mTriangleVertices.put(mTriangleVerticesData).position(0);

		mTriangleVertices1 = ByteBuffer.allocateDirect(FLOAT_SIZE_BYTES * mTriangleVerticesData1.length).order(ByteOrder.nativeOrder()).asFloatBuffer();
		mTriangleVertices1.put(mTriangleVerticesData1).position(0);
//		mTriangleVertices2 = ByteBuffer.allocateDirect(FLOAT_SIZE_BYTES * mTriangleVerticesData2.length).order(ByteOrder.nativeOrder()).asFloatBuffer();
//		mTriangleVertices2.put(mTriangleVerticesData2).position(0);
	}


	public int getTextureId() {
		return mTextureID;
	}

	public void drawFrame(SurfaceTexture st) {

		checkGlError("onDrawFrame start");

		if(f3<=0 && f4<=0){
			if(isSpliteScreen()){
				setOldData();
			}else{
				setSingleOldData();
			}
		}
		st.getTransformMatrix(mSTMatrix);

		GLES20.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
		GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
		GLES20.glUseProgram(mProgram);
		checkGlError("glUseProgram");
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, mTextureID);
		
		mTriangleVertices1.position(TRIANGLE_VERTICES_DATA_POS_OFFSET);
		GLES20.glVertexAttribPointer(maPositionHandle, 3, GLES20.GL_FLOAT, false, TRIANGLE_VERTICES_DATA_STRIDE_BYTES, mTriangleVertices1);
		checkGlError("glVertexAttribPointer maPosition");
		GLES20.glEnableVertexAttribArray(maPositionHandle);
		checkGlError("glEnableVertexAttribArray maPositionHandle");
		mTriangleVertices1.position(TRIANGLE_VERTICES_DATA_UV_OFFSET);
		GLES20.glVertexAttribPointer(maTextureHandle, 3, GLES20.GL_FLOAT, false, TRIANGLE_VERTICES_DATA_STRIDE_BYTES, mTriangleVertices1);
		checkGlError("glVertexAttribPointer maTextureHandle");
		GLES20.glEnableVertexAttribArray(maTextureHandle);
		checkGlError("glEnableVertexAttribArray maTextureHandle");
		Matrix.setIdentityM(mMVPMatrix, 0);
		GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, mMVPMatrix, 0);
		GLES20.glUniformMatrix4fv(muSTMatrixHandle, 1, false, mSTMatrix, 0);
		GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
		checkGlError("glDrawArrays");

		if(isSpliteScreen()){
			mTriangleVertices2.position(0);
			GLES20.glVertexAttribPointer(maPositionHandle, 3, 5126, false, 20, mTriangleVertices2);
			checkGlError("glVertexAttribPointer maPosition");
			GLES20.glEnableVertexAttribArray(maPositionHandle);
			checkGlError("glEnableVertexAttribArray maPositionHandle");
			mTriangleVertices2.position(3);
			GLES20.glVertexAttribPointer(maTextureHandle, 3, 5126, false, 20, mTriangleVertices2);
			checkGlError("glVertexAttribPointer maTextureHandle");
			GLES20.glEnableVertexAttribArray(maTextureHandle);
			checkGlError("glEnableVertexAttribArray maTextureHandle");
			Matrix.setIdentityM(mMVPMatrix, 0);
			GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, mMVPMatrix, 0);
			GLES20.glUniformMatrix4fv(muSTMatrixHandle, 1, false, mSTMatrix, 0);
			GLES20.glDrawArrays(5, 0, 4);
			checkGlError("glDrawArrays");
		}


		GLES20.glFinish();

		/*
		 * checkGlError("onDrawFrame start"); st.getTransformMatrix(mSTMatrix);
		 * GLES20.glClearColor(0.0f, 1.0f, 0.0f, 1.0f);
		 * GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT |
		 * GLES20.GL_COLOR_BUFFER_BIT); GLES20.glUseProgram(mProgram);
		 * checkGlError("glUseProgram");
		 * GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		 * GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, mTextureID);
		 * mTriangleVertices.position(TRIANGLE_VERTICES_DATA_POS_OFFSET);
		 * GLES20.glVertexAttribPointer(maPositionHandle, 3, GLES20.GL_FLOAT,
		 * false, TRIANGLE_VERTICES_DATA_STRIDE_BYTES, mTriangleVertices);
		 * checkGlError("glVertexAttribPointer maPosition");
		 * GLES20.glEnableVertexAttribArray(maPositionHandle);
		 * checkGlError("glEnableVertexAttribArray maPositionHandle");
		 * mTriangleVertices.position(TRIANGLE_VERTICES_DATA_UV_OFFSET);
		 * GLES20.glVertexAttribPointer(maTextureHandle, 2, GLES20.GL_FLOAT,
		 * false, TRIANGLE_VERTICES_DATA_STRIDE_BYTES, mTriangleVertices);
		 * checkGlError("glVertexAttribPointer maTextureHandle");
		 * GLES20.glEnableVertexAttribArray(maTextureHandle);
		 * checkGlError("glEnableVertexAttribArray maTextureHandle");
		 * Matrix.setIdentityM(mMVPMatrix, 0);
		 * GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, mMVPMatrix,
		 * 0); GLES20.glUniformMatrix4fv(muSTMatrixHandle, 1, false, mSTMatrix,
		 * 0); GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
		 * checkGlError("glDrawArrays"); GLES20.glFinish();
		 */
	}

	/**
	 * Initializes GL state. Call this after the EGL surface has been created
	 * and made current.
	 */
	public void surfaceCreated() {
		mProgram = createProgram(VERTEX_SHADER, FRAGMENT_SHADER);
		if (mProgram == 0) {
			throw new RuntimeException("failed creating program");
		}
		maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
		checkGlError("glGetAttribLocation aPosition");
		if (maPositionHandle == -1) {
			throw new RuntimeException("Could not get attrib location for aPosition");
		}
		maTextureHandle = GLES20.glGetAttribLocation(mProgram, "aTextureCoord");
		checkGlError("glGetAttribLocation aTextureCoord");
		if (maTextureHandle == -1) {
			throw new RuntimeException("Could not get attrib location for aTextureCoord");
		}
		muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
		checkGlError("glGetUniformLocation uMVPMatrix");
		if (muMVPMatrixHandle == -1) {
			throw new RuntimeException("Could not get attrib location for uMVPMatrix");
		}
		muSTMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uSTMatrix");
		checkGlError("glGetUniformLocation uSTMatrix");
		if (muSTMatrixHandle == -1) {
			throw new RuntimeException("Could not get attrib location for uSTMatrix");
		}
		int[] textures = new int[1];
		GLES20.glGenTextures(1, textures, 0);
		mTextureID = textures[0];
		GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, mTextureID);
		checkGlError("glBindTexture mTextureID");
		GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
		GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
		GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
		GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
		checkGlError("glTexParameter");
	}

	/**
	 * Replaces the fragment shader.
	 */
	public void changeFragmentShader(String fragmentShader) {
		GLES20.glDeleteProgram(mProgram);
		mProgram = createProgram(VERTEX_SHADER, fragmentShader);
		if (mProgram == 0) {
			throw new RuntimeException("failed creating program");
		}
	}

	private int loadShader(int shaderType, String source) {
		int shader = GLES20.glCreateShader(shaderType);
		checkGlError("glCreateShader type=" + shaderType);
		GLES20.glShaderSource(shader, source);
		GLES20.glCompileShader(shader);
		int[] compiled = new int[1];
		GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
		if (compiled[0] == 0) {
			Log.e(TAG, "Could not compile shader " + shaderType + ":");
			Log.e(TAG, " " + GLES20.glGetShaderInfoLog(shader));
			GLES20.glDeleteShader(shader);
			shader = 0;
		}
		return shader;
	}

	private int createProgram(String vertexSource, String fragmentSource) {
		int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexSource);
		if (vertexShader == 0) {
			return 0;
		}
		int pixelShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource);
		if (pixelShader == 0) {
			return 0;
		}
		int program = GLES20.glCreateProgram();
		checkGlError("glCreateProgram");
		if (program == 0) {
			Log.e(TAG, "Could not create program");
		}
		GLES20.glAttachShader(program, vertexShader);
		checkGlError("glAttachShader");
		GLES20.glAttachShader(program, pixelShader);
		checkGlError("glAttachShader");
		GLES20.glLinkProgram(program);
		int[] linkStatus = new int[1];
		GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);
		if (linkStatus[0] != GLES20.GL_TRUE) {
			Log.e(TAG, "Could not link program: ");
			Log.e(TAG, GLES20.glGetProgramInfoLog(program));
			GLES20.glDeleteProgram(program);
			program = 0;
		}
		return program;
	}

	public void checkGlError(String op) {
		int error;
		while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
			Log.e(TAG, op + ": glError " + error);
			throw new RuntimeException(op + ": glError " + error);
		}
	}
}
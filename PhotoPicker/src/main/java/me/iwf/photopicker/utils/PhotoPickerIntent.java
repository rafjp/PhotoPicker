package me.iwf.photopicker.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;

import me.iwf.photopicker.PhotoPickerActivity;

/**
 * Created by donglua on 15/7/2.
 */
public class PhotoPickerIntent extends Intent {

  private PhotoPickerIntent() {
  }

  private PhotoPickerIntent(Intent o) {
    super(o);
  }

  private PhotoPickerIntent(String action) {
    super(action);
  }

  private PhotoPickerIntent(String action, Uri uri) {
    super(action, uri);
  }

  private PhotoPickerIntent(Context packageContext, Class<?> cls) {
    super(packageContext, cls);
  }

  public PhotoPickerIntent(Context packageContext) {
    super(packageContext, PhotoPickerActivity.class);
  }

  public void setPhotoCount(int photoCount) {
    this.putExtra(PhotoPickerActivity.EXTRA_MAX_COUNT, photoCount);
  }

  public void setShowCamera(boolean showCamera) {
    this.putExtra(PhotoPickerActivity.EXTRA_SHOW_CAMERA, showCamera);
  }

  public void setShowGif(boolean showGif) {
    this.putExtra(PhotoPickerActivity.EXTRA_SHOW_GIF, showGif);
  }

  public void setColumn(int column) {
    this.putExtra(PhotoPickerActivity.EXTRA_GRID_COLUMN, column);
  }

  /**
   * By default a full sized image will be displayed when the image is tapped.
   * In order to turn this behaviour pass in a flase parameter
   *
   * @param showImageOnTap true if the full size image is to be displayed
   */
  public void setShowImageOnTap(boolean showImageOnTap) {
    this.putExtra(PhotoPickerActivity.EXTRA_SHOW_IMAGE_ON_TAP, showImageOnTap);
  }

  /**
   * By default the number of images is displayed next the the Done button.
   * Set the parameter to false so that only the Done button text is displayed
   *
   * @param showImageCount true if the number of images picked is to be displayed
   */
  public void setShowImageCount(boolean showImageCount) {
    this.putExtra(PhotoPickerActivity.EXTRA_SHOW_IMAGE_COUNT, showImageCount);
  }

  /**
   * By default the Gallery Selector button is display.
   * Set the parameter to false if the selector button is not to be displayed.
   *
   * @param showGallerySelector true if the Gallery Selector button is to be displayed
   */
  public void setShowGallerySelector(boolean showGallerySelector) {
    this.putExtra(PhotoPickerActivity.EXTRA_SHOW_GALLERY_SELECTOR, showGallerySelector);
  }

  /**
   *
   * @param selectedBorderColor the color to appear around the border when in selected mode
   */
  public void setSelectedBorderColor(@ColorInt int selectedBorderColor) {
    this.putExtra(PhotoPickerActivity.EXTRA_SELECTED_BORDER_COLOR, selectedBorderColor);
  }

  /**
   * @param backgroundColor the color to appear upon the picker display background
   */
  public void setBackgroundColor(@ColorInt int backgroundColor) {
    this.putExtra(PhotoPickerActivity.EXTRA_BACKGROUND_COLOR, backgroundColor);
  }

  public void setImagePadding(@DimenRes int imagePadding) {
    this.putExtra(PhotoPickerActivity.EXTRA_IMAGE_PADDING, imagePadding);
  }
}

package me.iwf.photopicker.adapter;

import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import me.iwf.photopicker.R;
import me.iwf.photopicker.entity.Photo;
import me.iwf.photopicker.entity.PhotoDirectory;
import me.iwf.photopicker.event.OnItemCheckListener;
import me.iwf.photopicker.event.OnPhotoClickListener;
import me.iwf.photopicker.utils.MediaStoreHelper;

/**
 * Created by donglua on 15/5/31.
 */
public class PhotoGridAdapter extends SelectableAdapter<PhotoGridAdapter.PhotoViewHolder> {

  private LayoutInflater inflater;

  private Context mContext;

  private OnItemCheckListener onItemCheckListener    = null;
  private OnPhotoClickListener onPhotoClickListener  = null;
  private View.OnClickListener onCameraClickListener = null;

  public final static int ITEM_TYPE_CAMERA = 100;
  public final static int ITEM_TYPE_PHOTO  = 101;
  private final static int COL_NUMBER_DEFAULT = 3;

  private boolean hasCamera = true;
  private boolean showImageOnTap = true;
  private @ColorInt int selectedBorderColor;
  private @DimenRes int imagePadding;

  private int imageSize;
  private int columnNumber = COL_NUMBER_DEFAULT;

  public PhotoGridAdapter(Context context, List<PhotoDirectory> photoDirectories) {
    this.photoDirectories = photoDirectories;
    this.mContext = context;
    inflater = LayoutInflater.from(context);
    setColumnNumber(context, columnNumber);
  }

  public PhotoGridAdapter(Context context, List<PhotoDirectory> photoDirectories, int colNum) {
    this(context, photoDirectories);
    setColumnNumber(context, colNum);
  }

  private void setColumnNumber(Context context, int columnNumber) {
    this.columnNumber = columnNumber;
    WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    DisplayMetrics metrics = new DisplayMetrics();
    wm.getDefaultDisplay().getMetrics(metrics);
    int widthPixels = metrics.widthPixels;
    imageSize = widthPixels / columnNumber;
  }

  @Override public int getItemViewType(int position) {
    return (showCamera() && position == 0) ? ITEM_TYPE_CAMERA : ITEM_TYPE_PHOTO;
  }


  @Override public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View itemView = inflater.inflate(R.layout.item_photo, parent, false);
    PhotoViewHolder holder = new PhotoViewHolder(itemView);
    if (viewType == ITEM_TYPE_CAMERA) {
      holder.vSelected.setVisibility(View.GONE);
      holder.ivPhoto.setScaleType(ImageView.ScaleType.CENTER);
      holder.ivPhoto.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View view) {
          if (onCameraClickListener != null) {
            onCameraClickListener.onClick(view);
          }
        }
      });
    }
    return holder;
  }


  @Override public void onBindViewHolder(final PhotoViewHolder holder, final int position) {

    if (getItemViewType(position) == ITEM_TYPE_PHOTO) {

      List<Photo> photos = getCurrentPhotos();
      final Photo photo;

      if (showCamera()) {
        photo = photos.get(position - 1);
      } else {
        photo = photos.get(position);
      }

      Glide.with(mContext)
              .load(new File(photo.getPath()))
              .centerCrop()
              .dontAnimate()
              .thumbnail(0.5f)
              .override(imageSize, imageSize)
              .placeholder(R.drawable.ic_photo_black_48dp)
              .error(R.drawable.ic_broken_image_black_48dp)
              .into(holder.ivPhoto);

      final boolean isChecked = isSelected(photo);

      int paddingPixels = holder.ivPhoto.getContext().getResources().getDimensionPixelSize(R.dimen.image_padding_dp_one);
      if (getImagePadding() != 0) {
        paddingPixels = holder.ivPhoto.getContext().getResources().getDimensionPixelSize(getImagePadding());
      }

      holder.ivPhoto.setPadding(paddingPixels, paddingPixels, paddingPixels, paddingPixels);

      holder.vSelected.setSelected(isChecked);
      holder.ivPhoto.setSelected(isChecked);

      holder.vgSelectionNumberContainer.setVisibility(!showImageOnTap && isChecked ? View.VISIBLE : View.GONE);
      holder.vgSelectionNumberContainer.setBackgroundColor(getSelectedBorderColor());

      if (isChecked) {
        int photoLabel = getSelectedPhotoPaths().indexOf(photo.getPath()) + 1;
        holder.tvSelectionNumber.setText(String.format(Locale.getDefault(), "%d", photoLabel));

        holder.ivPhoto.setBackgroundColor(getSelectedBorderColor());
      } else {
        holder.ivPhoto.setBackgroundResource(R.color.__picker_item_photo_border_n);
      }

      if (getShowImageOnTap()) {
        holder.vSelected.setVisibility(View.VISIBLE);
      } else {
        holder.vSelected.setVisibility(View.INVISIBLE);
      }

      holder.ivPhoto.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View view) {
          if (onPhotoClickListener != null && getShowImageOnTap()) {
            onPhotoClickListener.onClick(view, position, showCamera());

          } else if (!getShowImageOnTap())  {

            boolean isEnable = true;

            if (onItemCheckListener != null) {
              isEnable = onItemCheckListener.OnItemCheck(position, photo, isChecked, getSelectedPhotos().size());
            }
            if (isEnable) {
              toggleSelection(photo);
              notifyItemChanged(position);
            }

            if (!getShowImageOnTap()) {
              notifyDataSetChanged();
            }

          }
        }
      });
      holder.vSelected.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View view) {

          boolean isEnable = true;

          if (onItemCheckListener != null) {
            isEnable = onItemCheckListener.OnItemCheck(position, photo, isChecked,
                    getSelectedPhotos().size());
          }
          if (isEnable) {
            toggleSelection(photo);
            notifyItemChanged(position);
          }
        }
      });

    } else {
      holder.ivPhoto.setImageResource(R.drawable.__picker_camera);
      holder.vgSelectionNumberContainer.setVisibility(View.GONE);
    }
  }


  @Override public int getItemCount() {
    int photosCount =
            photoDirectories.size() == 0 ? 0 : getCurrentPhotos().size();
    if (showCamera()) {
      return photosCount + 1;
    }
    return photosCount;
  }


  public static class PhotoViewHolder extends RecyclerView.ViewHolder {
    private ImageView ivPhoto;
    private View vSelected;

    private View vgSelectionNumberContainer;
    private TextView tvSelectionNumber;

    public PhotoViewHolder(View itemView) {
      super(itemView);
      ivPhoto   = (ImageView) itemView.findViewById(R.id.iv_photo);
      vSelected = itemView.findViewById(R.id.v_selected);

      vgSelectionNumberContainer = itemView.findViewById(R.id.vg_photo_selection_number_container);
      tvSelectionNumber = (TextView) itemView.findViewById(R.id.tv_photo_selection_number);

      tvSelectionNumber.setText("");
    }
  }


  public void setOnItemCheckListener(OnItemCheckListener onItemCheckListener) {
    this.onItemCheckListener = onItemCheckListener;
  }


  public void setOnPhotoClickListener(OnPhotoClickListener onPhotoClickListener) {
    this.onPhotoClickListener = onPhotoClickListener;
  }


  public void setOnCameraClickListener(View.OnClickListener onCameraClickListener) {
    this.onCameraClickListener = onCameraClickListener;
  }


  public ArrayList<String> getSelectedPhotoPaths() {
    ArrayList<String> selectedPhotoPaths = new ArrayList<>(getSelectedItemCount());

    for (Photo photo : selectedPhotos) {
      selectedPhotoPaths.add(photo.getPath());
    }

    return selectedPhotoPaths;
  }


  public void setShowCamera(boolean hasCamera) {
    this.hasCamera = hasCamera;
  }


  public boolean showCamera() {
    return (hasCamera && currentDirectoryIndex == MediaStoreHelper.INDEX_ALL_PHOTOS);
  }


  public void setShowImageOnTap(boolean showImageOnTap) {
    this.showImageOnTap = showImageOnTap;
  }
  public boolean getShowImageOnTap() {
    return this.showImageOnTap;
  }

  public void setSelectedBorderColor(@ColorInt int selectedBorderColor) {
    this.selectedBorderColor = selectedBorderColor;
  }
  public @ColorInt int getSelectedBorderColor() {
    return this.selectedBorderColor;
  }

  public void setImagePadding(@DimenRes int imagePadding) {
    this.imagePadding = imagePadding;
  }
  public @DimenRes int getImagePadding() {
    return this.imagePadding;
  }
}

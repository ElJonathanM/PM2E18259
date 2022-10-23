using Plugin.Media;
using PM2E18259.Services;
using PM2E18259.Views;
using System;
using System.Collections;
using System.Collections.Generic;
using System.ComponentModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Xamarin.Essentials;
using Xamarin.Forms;

namespace PM2E18259
{
    public partial class MainPage : ContentPage
    {
        String direccion;
        public MainPage()
        {
            InitializeComponent();
            ubicacion();
        }
        private async void TomarFoto_Clicked(object sender, EventArgs e)
        {
            var takepic = await CrossMedia.Current.TakePhotoAsync(new Plugin.Media.Abstractions.StoreCameraMediaOptions
            {
                Directory = "PhotoApp",
                Name = "TEST.jpg"
            });
            direccion = takepic.Path;


            if (takepic != null)
            {
                foto.Source = ImageSource.FromStream(() => { return takepic.GetStream(); });
            }
            var Sharephoto = takepic.Path;
            await Share.RequestAsync(new ShareFileRequest
            {
                Title = "Foto",
                File = new ShareFile(Sharephoto)
            });
        }

        private async void Galeria_Clicked(object sender, EventArgs e)
        {
            if (!CrossMedia.Current.IsPickPhotoSupported)
            {
                await DisplayAlert("Lo siento!!", "Error de permisos", "Configura");
                return;
            }
            var file = await Plugin.Media.CrossMedia.Current.PickPhotoAsync(new Plugin.Media.Abstractions.PickMediaOptions
            {
                PhotoSize = Plugin.Media.Abstractions.PhotoSize.Medium,

            });

            direccion = file.Path;

            if (file != null)
            {
                foto.Source = ImageSource.FromStream(() => { return file.GetStream(); });
                return;
            }
            var Sharephoto = file.Path;
            await Share.RequestAsync(new ShareFileRequest
            {
                Title = "Foto",
                File = new ShareFile(Sharephoto)
            });
        }

        private async void Agregar_Clicked(object sender, EventArgs e)
        {
                if (direccion == "" || String.IsNullOrEmpty(txtDes.Text) || String.IsNullOrEmpty(lblLat.Text))
                {
                    await DisplayAlert("Lo siento!!", "No se puede agregar el registro si no tiene imagen, latitud, longitud o descripción (Recuerde encender la ubicación del telefono)","Ingresa tus datos");

                }
                else
                {
                    var emple = new Lugar
                    {
                        latitud = lblLat.Text,
                        longitud = lblLon.Text,
                        descripcion = txtDes.Text,
                        image = direccion
                    };
                    var resultado = await App.BaseDatos.EmpleadoGuardar(emple);
                    if (resultado != 0)
                    {
                        await DisplayAlert("AVISO!!", "Lugar guardado con éxito!", "OK");
                        foto.Source = ("");
                        direccion = "";
                        txtDes.Text = "";
                        ubicacion();

                    }
                    else
                    {
                        await DisplayAlert("LO SIENTO", "Debe encender la ubiación", "OK");
                    }
                }
         
            
        }

        

        private async void Lista_Clicked(object sender, EventArgs e)
        {
            var newpage = new ListEmple();
            await Navigation.PushAsync(newpage);
        }

        private async void Eliminar_Clicked(object sender, EventArgs e)
        {
            if (String.IsNullOrEmpty(lblCod.Text))
            {
                await DisplayAlert("LO SIENTO", "No se puede actualizar si esta no es una vista", "OK");
            }
            else
            {
                var emple = new Lugar
                {
                    id = Convert.ToInt32(lblCod.Text)
                };
                var resultado = await App.BaseDatos.EmpleadoBorrar(emple);
                if (resultado != 0)
                {
                    await DisplayAlert("AVISO", "El lugar ha sido eliminado!", "OK");
                    await Navigation.PopAsync();
                }
                else
                {
                    await DisplayAlert("LO SIENTO", "Error al eliminar estos datos", "OK");
                }

            }
        }

        private void Salir_Clicked(object sender, EventArgs e)
        {
            System.Diagnostics.Process.GetCurrentProcess().Kill();
        }


        //Clases
        async private void ubicacion()
        {
            try
            {
                var location = await Geolocation.GetLastKnownLocationAsync();

                if (location != null)
                {
                    lblLat.Text = location.Latitude.ToString();
                    lblLon.Text = location.Longitude.ToString();
                }
            }
            catch (FeatureNotSupportedException fnsEx) {  }

        }
        public bool EmptyField(Entry campo)
        {
            return String.IsNullOrEmpty(campo.Text);
        }

    }
}

using Plugin.Media;
using PM2E18259.Services;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Xamarin.Essentials;
using Xamarin.Forms;
using Xamarin.Forms.Xaml;

namespace PM2E18259.Views
{
    [XamlCompilation(XamlCompilationOptions.Compile)]
    public partial class VerRegistro : ContentPage
    {
        String longitud, latitud;
        public VerRegistro()
        {
            InitializeComponent();
            longitud = lblLon.Text;
            latitud = lblLat.Text;
        }

        private async void Eliminar_Clicked(object sender, EventArgs e)
        {
            if (String.IsNullOrEmpty(lblCod.Text))
            {
                await DisplayAlert("LO SIENTO!!", "No se puede actualizar si esta no es una vista", "OK");
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
                    await DisplayAlert("AVISO!", "Lugar eliminado con éxito!", "OK");
                    await Navigation.PopAsync();
                }
                else
                {
                    await DisplayAlert("LO SIENTO!", "Error al eliminar estos datos", "OK");
                }

            }
        }

        private async void Lista_Clicked(object sender, EventArgs e)
        {
            var newpage = new ListEmple();
            await Navigation.PushAsync(newpage);
        }

        protected override void OnAppearing()
        {
            base.OnAppearing();
        }

        private async void Enviar_Clicked(object sender, EventArgs e)
        {
            try
            {
                await Share.RequestAsync(
                   new ShareTextRequest
                   {
                       Title = "Te comparto la siguiente ubicación",
                       Text = "Este es el lugar donde me tome la fotografia",
                       Uri = "https://maps.google.com/?q=" + longitud + "," + latitud
                   }
                    );
            }
            catch(Exception)
            {

            }
        }
    }
}
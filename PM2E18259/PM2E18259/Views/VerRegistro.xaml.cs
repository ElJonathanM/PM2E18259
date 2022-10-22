using PM2E18259.Services;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

using Xamarin.Forms;
using Xamarin.Forms.Xaml;

namespace PM2E18259.Views
{
    [XamlCompilation(XamlCompilationOptions.Compile)]
    public partial class VerRegistro : ContentPage
    {
        public VerRegistro()
        {
            InitializeComponent();
        }

        private async void Eliminar_Clicked(object sender, EventArgs e)
        {
            if (String.IsNullOrEmpty(lblCod.Text))
            {
                await DisplayAlert("Oops", "No se puede actualizar si esta no es una vista", "OK");
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
                    await DisplayAlert("Aviso", "Lugar eliminado!", "OK");
                    await Navigation.PopAsync();
                }
                else
                {
                    await DisplayAlert("Ooops", "Error al eliminar estos datos", "OK");
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
    }
}
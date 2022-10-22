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
    public partial class ListEmple : ContentPage
    {
        public ListEmple()
        {
            InitializeComponent();
        }

        private async void ListaEmpleados_ItemTapped(object sender, ItemTappedEventArgs e)
        {
            String sexResult = await DisplayActionSheet("¿Qué desea hacer? ", "Cancelar", null, "Ver Registro", "Geolocalización");


            switch (sexResult)
            {
                case "Ver Registro":
                    Services.Lugar item = (Services.Lugar)e.Item;
                    var newpage = new VerRegistro();
                    newpage.BindingContext = item;
                    await Navigation.PushAsync(newpage);
                    break;

                case "Geolocalización":

                    Services.Lugar item2 = (Services.Lugar)e.Item;
                    var newpage2 = new MapaEmple();
                    newpage2.BindingContext = item2;
                    await Navigation.PushAsync(newpage2);

                    break;
                default:
                    break;
            }
        }
        protected override async void OnAppearing()
        {
            base.OnAppearing();
            ListaEmpleados.ItemsSource = await App.BaseDatos.listaempleados();
        }
    }
}
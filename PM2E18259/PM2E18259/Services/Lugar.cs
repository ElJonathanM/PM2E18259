using SQLite;
using System;
using System.Collections.Generic;
using System.Text;

namespace PM2E18259.Services
{
    public class Lugar
    {
        [PrimaryKey, AutoIncrement]
        public int id { get; set; }
        public string latitud { get; set; }
        public string longitud { get; set; }
        public string descripcion { get; set; }
        public string image { get; set; }
    }
}

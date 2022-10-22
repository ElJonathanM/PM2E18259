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
        public string latitudC { get; set; }
        public string longitudC { get; set; }
        public string descripcionC { get; set; }
        public string imageC { get; set; }
    }
}

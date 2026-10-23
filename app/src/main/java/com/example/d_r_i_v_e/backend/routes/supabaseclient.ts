import { createClient } from '@supabase/supabase-js';

const SUPABASE_URL = 'https://dwetyttqopsfnkiqaobz.supabase.co';
const SUPABASE_KEY = 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImR3ZXR5dHRxb3BzZm5raXFhb2J6Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3OTIzODMwNDcsImV4cCI6MjEwNzk1OTA0N30.MgnCFGzviQxiyT46LnaKvg5dOXWpU0edmDOdcszuDBw';

export const supabase = createClient(SUPABASE_URL, SUPABASE_KEY);
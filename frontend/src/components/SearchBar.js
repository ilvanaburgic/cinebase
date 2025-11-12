export default function SearchBar({ value, onChange, onSubmit }) {
    return (
        <form onSubmit={onSubmit} className="searchbar">
            <input
                type="text"
                aria-label="Search"
                placeholder="Search for movie, TV show, person"
                value={value}
                onChange={(e)=>onChange(e.target.value)}
            />
        </form>
    );
}

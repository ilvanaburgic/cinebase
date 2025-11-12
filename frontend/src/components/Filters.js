export default function Filters({ active, onChange }) {
    const tabs = [
        { key: "popular", label: "Most popular" },
        { key: "latest",  label: "Latest" },
        { key: "top",     label: "Highest rate" },
    ];
    return (
        <div className="filters">
            {tabs.map(t => (
                <button
                    key={t.key}
                    className={active === t.key ? "active" : ""}
                    onClick={()=>onChange(t.key)}
                    type="button"
                >
                    {t.label}
                </button>
            ))}
        </div>
    );
}

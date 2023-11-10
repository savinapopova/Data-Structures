import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;


public class RoyaleArena implements IArena {

    private Map<Integer, Battlecard> cardsById;
    private Map<CardType, Set<Battlecard>> cardsByType;
    private Set<Battlecard> battleCards;

    public RoyaleArena() {
        this.cardsById = new LinkedHashMap<>();
        this.battleCards = new LinkedHashSet<>();
        this.cardsByType = new LinkedHashMap<>();
    }

    @Override
    public void add(Battlecard card) {
        cardsById.putIfAbsent(card.getId(), card);
        this.cardsByType.putIfAbsent(card.getType(),
                new TreeSet<>(Battlecard::compareTo));
        cardsByType.get(card.getType()).add(card);

    }

    @Override
    public boolean contains(Battlecard card) {
        return cardsById.containsKey(card.getId());
    }

    @Override
    public int count() {
         return cardsById.size();
    }

    @Override
    public void changeCardType(int id, CardType type) {
        Battlecard battlecard = cardsById.get(id);
        if (battlecard == null) {
            throw new IllegalArgumentException();
        }
        battlecard.setType(type);


    }

    @Override
    public Battlecard getById(int id) {
        Battlecard battlecard = cardsById.get(id);
        if (battlecard == null) {
            throw new UnsupportedOperationException();
        }
        return battlecard;
    }

    @Override
    public void removeById(int id) {
        Battlecard battlecard = cardsById.remove(id);
        if (battlecard == null) {
            throw new UnsupportedOperationException();
        }
        cardsByType.get(battlecard.getType()).remove(battlecard);
    }

    @Override
    public Iterable<Battlecard> getByCardType(CardType type) {
        return getBattlecards(type);

    }

    @Override
    public Iterable<Battlecard>
    getByTypeAndDamageRangeOrderedByDamageThenById(CardType type, int lo, int hi) {
        Set<Battlecard> battlecards = getBattlecards(type);
        List<Battlecard> result = battlecards.stream()
                .filter(c -> c.getDamage() > lo && c.getDamage() < hi)
                .sorted(Battlecard::compareTo)
                .collect(Collectors.toList());

        if (result.isEmpty()) {
            throw new UnsupportedOperationException();
        }
        return result;
    }



    @Override
    public Iterable<Battlecard>
    getByCardTypeAndMaximumDamage(CardType type, double damage) {
        Set<Battlecard> battlecards = getBattlecards(type);
        List<Battlecard> result =
                battlecards.stream().filter(c -> c.getDamage() <= damage)
                        .sorted(Battlecard::compareTo)
                .collect(Collectors.toList());
        if (result.isEmpty()) {
            throw  new UnsupportedOperationException();
        }
        return result;
    }

    @Override
    public Iterable<Battlecard> getByNameOrderedBySwagDescending(String name) {
        List<Battlecard> battlecards =
                getBattlecardsByPredicate( c -> c.getName().equals(name));

        if (battlecards.isEmpty()) {
            throw new UnsupportedOperationException();
        }

        battlecards.sort(Comparator.comparingDouble(Battlecard::getSwag).reversed()
                .thenComparing(Battlecard::getId));
        return battlecards;
    }

    @Override
    public Iterable<Battlecard>
    getByNameAndSwagRange(String name, double lo, double hi) {
        List<Battlecard> battlecards =
                getBattlecardsByPredicate(c -> c.getDamage() >= lo
                && c.getDamage() < hi && c.getName().equals(name));
        if (battlecards.isEmpty()) {
            throw new UnsupportedOperationException();
        }
        battlecards.sort(Comparator.comparingDouble(Battlecard::getSwag).reversed()
                .thenComparing(Battlecard::getId));

        return battlecards;
    }

    @Override
    public Iterable<Battlecard> getAllByNameAndSwag() {
        Map<String, Battlecard> battlecards = new LinkedHashMap<>();
        for (Battlecard battlecard : cardsById.values()) {
            if (!battlecards.containsKey(battlecard.getName())) {
                battlecards.put(battlecard.getName(), battlecard);
            } else {
                double oldSwag = battlecards.get(battlecard.getName()).getSwag();
                double newSwag = battlecard.getSwag();
                if (newSwag > oldSwag) {
                    battlecards.put(battlecard.getName(), battlecard);
                }
            }
        }

        return battlecards.values();
    }

    @Override
    public Iterable<Battlecard> findFirstLeastSwag(int n) {
        if (n > count()) {
            throw new UnsupportedOperationException();
        }

        List<Battlecard> battlecards = cardsById.values().stream()
                .sorted(Comparator.comparingDouble(Battlecard::getSwag))
                .collect(Collectors.toList());


        return battlecards.stream().limit(n)
                .sorted(Comparator.comparingDouble(Battlecard::getSwag)
                        .thenComparing(Battlecard::getId))
                .collect(Collectors.toList());

    }

    @Override
    public Iterable<Battlecard> getAllInSwagRange(double lo, double hi) {
        return getBattlecardsByPredicate(c -> c.getSwag() >= lo && c.getSwag() <= hi)
                .stream().sorted(Comparator.comparingDouble(Battlecard::getSwag))
                .collect(Collectors.toList());
    }

    @Override
    public Iterator<Battlecard> iterator() {
        return this.cardsById.values().iterator();
    }

    private Set<Battlecard> getBattlecards(CardType type) {
        Set<Battlecard> battlecards = cardsByType.get(type);

        if (battlecards == null || battlecards.isEmpty()) {
            throw new UnsupportedOperationException();
        }
        return battlecards;
    }

    private List<Battlecard>
    getBattlecardsByPredicate(Predicate<Battlecard> predicate) {
        List<Battlecard> battlecards = new ArrayList<>();

        for (Battlecard battlecard : cardsById.values()) {
            if (predicate.test(battlecard))
                battlecards.add(battlecard);
        }
        return battlecards;
    }
}
